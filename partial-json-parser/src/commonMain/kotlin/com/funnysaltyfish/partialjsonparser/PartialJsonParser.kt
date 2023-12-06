package com.funnysaltyfish.partialjsonparser

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
        val tokens = Tokenizer.tokenize(str)
        return Parser.parseTokens(tokens)
    }
}