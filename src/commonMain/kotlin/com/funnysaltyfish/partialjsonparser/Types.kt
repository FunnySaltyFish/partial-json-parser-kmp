package com.funnysaltyfish.partialjsonparser

/**
 * export interface ParseContext {
 *   source: string;
 *   index: number;
 *   tokens: Token[];
 * }
 *
 * export interface Token {
 *   type: TokenType;
 *   value?: string | number | boolean | null;
 * }
 *
 * export type Keyword = 'true' | 'false' | 'null';
 */

data class ParseContext(
    var source: String,
    var index: Int,
    var tokens: MutableList<Token>
)

abstract class Token(val type: TokenType) {
    abstract val value: Any?

    class StringToken(override val value: String): Token(TokenType.String)
    class NumberToken(override val value: Number): Token(TokenType.Number)
    class BooleanToken(override val value: Boolean): Token(TokenType.Boolean)
    object NullToken: Token(TokenType.Null) {
        override val value: Any? = null
    }

    companion object {
        fun from(type: TokenType, value: Any? = null): Token {
            return when (type) {
                TokenType.String -> StringToken(value as String)
                TokenType.Number -> NumberToken(value as Number)
                TokenType.Boolean -> BooleanToken(value as Boolean)
                TokenType.Null -> NullToken
                else -> object : Token(type) {
                    override val value: Any? = value
                }
            }
        }
    }

}

typealias Keyword = String