package com.funnysaltyfish.partialjsonparser.parser

import com.funnysaltyfish.partialjsonparser.JsonParseException
import com.funnysaltyfish.partialjsonparser.Token
import com.funnysaltyfish.partialjsonparser.TokenType

object Parser {
    /**
     * import { Token } from '../types';
     * import { TokenType } from '../constant';
     *
     * function parseArray(tokens: Token[]) {
     *   let result: any[] = [];
     *   while (tokens.length > 0) {
     *     switch (tokens[0].type) {
     *       case TokenType.RightBracket:
     *         tokens.shift();
     *         return result;
     *       case TokenType.Comma:
     *         tokens.shift();
     *         break;
     *       default:
     *         result.push(parseTokens(tokens));
     *         break;
     *     }
     *   }
     *   return result;
     * }
     *
     */

    fun parseArray(tokens: MutableList<Token>): List<Any?> {
        val result = mutableListOf<Any?>()
        while (tokens.isNotEmpty()) {
            when (tokens[0].type) {
                TokenType.RightBracket -> {
                    tokens.removeFirst()
                    return result
                }
                TokenType.Comma -> {
                    tokens.removeFirst()
                }
                else -> {
                    result.add(parseTokens(tokens))
                }
            }
        }
        return result
    }

    /*
     *
     * function parseObject(tokens: Token[]) {
     *   let result: Record<string, any> = {};
     *   let key: string | undefined;
     *   let value: any;
     *   while (tokens.length > 0) {
     *     switch (tokens[0].type) {
     *       case TokenType.RightBrace:
     *         tokens.shift();
     *         return result;
     *       case TokenType.Comma:
     *         tokens.shift();
     *         break;
     *       case TokenType.Colon:
     *         tokens.shift();
     *         if (tokens.length > 0) {
     *           value = parseTokens(tokens);
     *           result[key!] = value;
     *         }
     *         break;
     *       case TokenType.String:
     *         key = tokens.shift()!.value as string;
     *         break;
     *       default:
     *         throw new Error(`Invalid JSON String`);
     *     }
     *   }
     *   return result;
     * */

    fun parseObject(tokens: MutableList<Token>): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        var key: String? = null
        var value: Any?
        while (tokens.isNotEmpty()) {
            when (tokens[0].type) {
                TokenType.RightBrace -> {
                    tokens.removeFirst()
                    return result
                }

                TokenType.Comma -> {
                    tokens.removeFirst()
                }

                TokenType.Colon -> {
                    tokens.removeFirst()
                    if (tokens.isNotEmpty()) {
                        value = parseTokens(tokens)
                        result[key!!] = value
                    }
                }

                TokenType.String -> {
                    key = (tokens.removeFirst() as Token.StringToken).value
                }

                else -> {
                    throw JsonParseException("Invalid JSON String")
                }
            }
        }
        return result
    }

     /*
     * }
     * export function parseTokens(tokens: Token[]) {
     *   const token = tokens.shift()!;
     *   switch (token.type) {
     *     case TokenType.Boolean:
     *     case TokenType.Number:
     *     case TokenType.Null:
     *     case TokenType.String:
     *       return token.value;
     *     case TokenType.LeftBracket:
     *       return parseArray(tokens);
     *     case TokenType.LeftBrace:
     *       return parseObject(tokens);
     *     default:
     *       throw new Error(`Invalid JSON String`);
     *   }
     *   throw new Error(`Invalid JSON String`);
     * }
     *
     */

    fun parseTokens(tokens: MutableList<Token>): Any? {
        val token = tokens.removeFirst()
        when (token.type) {
            TokenType.Boolean,
            TokenType.Number,
            TokenType.Null,
            TokenType.String -> {
                return token.value
            }

            TokenType.LeftBracket -> {
                return parseArray(tokens)
            }

            TokenType.LeftBrace -> {
                return parseObject(tokens)
            }

            else -> {
                throw JsonParseException("Invalid JSON String")
            }
        }
    }
}