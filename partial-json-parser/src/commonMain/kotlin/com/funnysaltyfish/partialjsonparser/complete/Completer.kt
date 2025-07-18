package com.funnysaltyfish.partialjsonparser.complete

import com.funnysaltyfish.partialjsonparser.Token
import com.funnysaltyfish.partialjsonparser.TokenType

object Completer {
    
    fun completeTokens(tokens: MutableList<Token>, originalString: String): String {
        if (tokens.isEmpty()) return "{}"
        
        // 过滤掉最后不完整的 token
        val validTokens = filterValidTokens(tokens, originalString)
        
        // 基于有效的 tokens 构建 JSON
        return buildCompleteJson(validTokens)
    }
    
    private fun filterValidTokens(tokens: MutableList<Token>, originalString: String): MutableList<Token> {
        val validTokens = mutableListOf<Token>()
        var i = 0
        
        while (i < tokens.size) {
            val token = tokens[i]
            
            when (token.type) {
                TokenType.LeftBrace, TokenType.LeftBracket, 
                TokenType.RightBrace, TokenType.RightBracket,
                TokenType.Comma, TokenType.Colon,
                TokenType.Number, TokenType.Boolean, TokenType.Null -> {
                    validTokens.add(token)
                }
                TokenType.String -> {
                    // 特殊处理字符串 token
                    val context = analyzeStringContext(validTokens, i, tokens)
                    when (context) {
                        StringContext.CompleteKey -> {
                            validTokens.add(token)
                        }
                        StringContext.CompleteValue -> {
                            validTokens.add(token)
                        }
                        StringContext.IncompleteKey -> {
                            // 忽略不完整的键，停止处理
                            break
                        }
                        StringContext.IncompleteValue -> {
                            // 保留不完整的值
                            validTokens.add(token)
                        }
                    }
                }
            }
            i++
        }
        
        return validTokens
    }
    
    private fun analyzeStringContext(validTokens: MutableList<Token>, currentIndex: Int, allTokens: MutableList<Token>): StringContext {
        val inObject = isInObjectContext(validTokens)
        val expectingKey = isExpectingKey(validTokens)
        
        if (inObject && expectingKey) {
            // 在对象中且期望键
            val nextTokenIndex = currentIndex + 1
            if (nextTokenIndex < allTokens.size && allTokens[nextTokenIndex].type == TokenType.Colon) {
                return StringContext.CompleteKey
            } else {
                return StringContext.IncompleteKey
            }
        } else {
            // 在数组中或作为值
            if (currentIndex == allTokens.size - 1) {
                // 最后一个 token，可能是不完整的值
                return StringContext.IncompleteValue
            } else {
                return StringContext.CompleteValue
            }
        }
    }
    
    private enum class StringContext {
        CompleteKey,     // 完整的对象键
        IncompleteKey,   // 不完整的对象键
        CompleteValue,   // 完整的值
        IncompleteValue  // 不完整的值
    }
    
    private fun isExpectingKey(validTokens: MutableList<Token>): Boolean {
        if (validTokens.isEmpty()) return false
        
        // 查看最后几个 token 来判断是否期望一个键
        val lastToken = validTokens.last()
        if (lastToken.type == TokenType.LeftBrace) {
            return true // 刚开始一个对象
        }
        if (lastToken.type == TokenType.Comma) {
            // 检查是否在对象中的逗号
            return isInObjectContext(validTokens)
        }
        
        return false
    }
    
    private fun isInObjectContext(tokens: MutableList<Token>): Boolean {
        val stack = mutableListOf<TokenType>()
        
        for (token in tokens) {
            when (token.type) {
                TokenType.LeftBrace -> {
                    stack.add(TokenType.LeftBrace)
                }
                TokenType.RightBrace -> {
                    if (stack.isNotEmpty() && stack.last() == TokenType.LeftBrace) {
                        stack.removeLast()
                    }
                }
                TokenType.LeftBracket -> {
                    stack.add(TokenType.LeftBracket)
                }
                TokenType.RightBracket -> {
                    if (stack.isNotEmpty() && stack.last() == TokenType.LeftBracket) {
                        stack.removeLast()
                    }
                }
                else -> {}
            }
        }
        
        // 检查最顶层的未闭合结构是否是对象
        return stack.isNotEmpty() && stack.last() == TokenType.LeftBrace
    }
    
    private fun buildCompleteJson(tokens: MutableList<Token>): String {
        val sb = StringBuilder()
        var i = 0
        val stack = mutableListOf<StackItem>()
        
        while (i < tokens.size) {
            val token = tokens[i]
            when (token.type) {
                TokenType.LeftBrace -> {
                    sb.append("{")
                    stack.add(StackItem.Object)
                }
                TokenType.RightBrace -> {
                    sb.append("}")
                    if (stack.isNotEmpty() && stack.last() == StackItem.Object) {
                        stack.removeLast()
                    }
                }
                TokenType.LeftBracket -> {
                    sb.append("[")
                    stack.add(StackItem.Array)
                }
                TokenType.RightBracket -> {
                    sb.append("]")
                    if (stack.isNotEmpty() && stack.last() == StackItem.Array) {
                        stack.removeLast()
                    }
                }
                TokenType.String -> {
                    sb.append("\"").append(escapeString(token.value.toString())).append("\"")
                }
                TokenType.Number -> {
                    sb.append(formatNumber(token.value as Number))
                }
                TokenType.Boolean -> {
                    sb.append(token.value.toString())
                }
                TokenType.Null -> {
                    sb.append("null")
                }
                TokenType.Comma -> {
                    sb.append(",")
                }
                TokenType.Colon -> {
                    sb.append(":")
                }
            }
            i++
        }
        
        // 补全逻辑：分析当前状态并决定如何补全
        return completeJsonString(sb.toString(), stack)
    }
    
    private fun completeJsonString(partial: String, stack: MutableList<StackItem>): String {
        val sb = StringBuilder(partial)
        
        // 检查是否需要补全值
        if (partial.endsWith(":")) {
            // 如果以冒号结尾，移除不完整的键值对
            // 向前查找到最近的键，然后移除整个键值对
            removeIncompleteKeyValuePair(sb)
        } else if (partial.endsWith(",")) {
            // 如果以逗号结尾，移除逗号（因为后面没有完整的元素）
            sb.deleteAt(sb.length - 1)
        }
        
        // 关闭所有开放的括号
        while (stack.isNotEmpty()) {
            when (stack.removeLast()) {
                StackItem.Object -> sb.append("}")
                StackItem.Array -> sb.append("]")
            }
        }
        
        return sb.toString()
    }
    
    private fun removeIncompleteKeyValuePair(sb: StringBuilder) {
        // 向前查找到最近的键开始位置
        var i = sb.length - 1
        
        // 跳过冒号
        if (i >= 0 && sb[i] == ':') {
            i--
        }
        
        // 跳过空格
        while (i >= 0 && sb[i].isWhitespace()) {
            i--
        }
        
        // 应该是字符串的结束引号
        if (i >= 0 && sb[i] == '"') {
            i--
            // 向前查找字符串的开始引号
            while (i >= 0) {
                if (sb[i] == '"') {
                    // 检查是否被转义
                    var escaped = false
                    var j = i - 1
                    while (j >= 0 && sb[j] == '\\') {
                        escaped = !escaped
                        j--
                    }
                    if (!escaped) {
                        // 找到了字符串开始，继续向前查找到键的开始位置
                        i--
                        // 跳过空格
                        while (i >= 0 && sb[i].isWhitespace()) {
                            i--
                        }
                        // 应该是逗号或左大括号
                        if (i >= 0 && (sb[i] == ',' || sb[i] == '{')) {
                            if (sb[i] == ',') {
                                // 删除包括逗号在内的整个键值对
                                sb.deleteRange(i, sb.length)
                            } else {
                                // 删除键值对，但保留左大括号
                                sb.deleteRange(i + 1, sb.length)
                            }
                        }
                        break
                    }
                }
                i--
            }
        }
    }
    
    private fun formatNumber(number: Number): String {
        return when (number) {
            is Int -> number.toString()
            is Long -> number.toString()
            is Float -> {
                if (number % 1.0f == 0.0f) {
                    number.toInt().toString()
                } else {
                    number.toString()
                }
            }
            is Double -> {
                if (number % 1.0 == 0.0) {
                    number.toLong().toString()
                } else {
                    number.toString()
                }
            }
            else -> number.toString()
        }
    }
    
    private fun escapeString(str: String): String {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t")
    }
    
    private enum class StackItem {
        Object, Array
    }
}