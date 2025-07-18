package com.funnysaltyfish.partialjsonparser

import com.funnysaltyfish.partialjsonparser.complete.Completer
import com.funnysaltyfish.partialjsonparser.parser.Parser
import com.funnysaltyfish.partialjsonparser.tokenizer.Tokenizer

object PartialJsonParser {
    /**
     * Parse an incomplete JSON directly, the output should either be [String], [Boolean], [Number], `null`, [List] or [Map]
     *
     * Some examples:
     * ```plain
     * {"key"                    -> {}
     * {"key":                   -> {}
     * {"key":"te                -> {key=te}
     * {"key":"text", "key2      -> {key=text}
     * {"key":"text", "key2":"t} -> {key=text, key2=t}}
     * ```
     *
     * Adapted from [Here](https://github.com/SimonTart/json-fragment-parser) by GitHub Copilot
     *
     * @param str incomplete JSON string
     * @return parsed result, normally a [Map] (`{...}`) or [List] (`[...]`)
     * @throws SyntaxException
     * @throws JsonParseException
     */
    fun parse(str: String): Any? {
        if (str.isEmpty()) return null
        val tokens = Tokenizer.tokenize(str)
        return Parser.parseTokens(tokens)
    }

    /**
     * Complete an incomplete JSON string to a valid JSON string, ignore all incomplete key-value pairs
     *
     * Some examples:
     * ```plain
     * {"key"                    -> {}
     * {"key":                   -> {}
     * {"key":"te                -> {"key":"te"}
     * {"key":"text", "key2      -> {"key":"text"}
     * {"key":"text", "key2":"t} -> {"key":"text","key2":"t"}
     * ```
     *
     * @param str incomplete JSON string
     * @return completed(repaired) JSON string
     * @throws SyntaxException
     * @throws JsonParseException
     */
    fun complete(str: String): String {
        if (str.isEmpty()) return "{}"
        val tokens = Tokenizer.tokenize(str)
        return Completer.completeTokens(tokens, str)
    }
}