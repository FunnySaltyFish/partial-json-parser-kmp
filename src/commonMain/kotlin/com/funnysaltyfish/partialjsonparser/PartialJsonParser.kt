package com.funnysaltyfish.partialjsonparser

import com.funnysaltyfish.partialjsonparser.parser.Parser
import com.funnysaltyfish.partialjsonparser.tokenizer.Tokenizer

object PartialJsonParser {
    /**
     * Parse an incomplete JSON directly, the output should either be [String], [Boolean], [Number], `null`, [List] or [Map]
     *
     * @param str
     * @return
     */
    fun parse(str: String): Any? {
        val tokens = Tokenizer.tokenize(str)
        return Parser.parseTokens(tokens)
    }
}