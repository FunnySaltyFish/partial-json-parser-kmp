package com.funnysaltyfish.partialjsonparser.tokenizer

import com.funnysaltyfish.partialjsonparser.*

object Tokenizer {
    /*
    import { Keyword, ParseContext } from '../types';
    import { isEnd, isIndexEnd } from './utils';
    import { isWhitespace } from '../char';
    import { isNumberStart, parseNumber } from './parseNumber';
    import { isStringrStart, parseString } from './parseString';
    import { TokenType } from '../constant';

    function skipSpaces(contxt: ParseContext) {
      const { index, source } = contxt;
      let i: number;

      for (i = index; i < source.length; i++) {
        const char = source[i] as string;

        if (!isWhitespace(char)) {
          break;
        }
      }

      contxt.index = i;
    }
    */

    internal fun skipSpaces(context: ParseContext) {
        val (source, index) = context
        var i: Int = index

        while (i < source.length) {
            val char = source[i]
            if (!char.isWhitespace()) {
                break
            }
            i++
        }

        context.index = i
    }

        /*
    function parseKeyword(context: ParseContext, keyword: Keyword) {
      const { source, index } = context;

      let i = 0;
      while (!isIndexEnd(context, i) && i < keyword.length) {
        if (source[index + i] !== keyword[i]) {
          throw new SyntaxError(`Failed to parse value at position: ${index}`);
        }
        if (i === keyword.length - 1) {
          break;
        } else {
          i++;
        }
      }
      let tokenType: TokenType;
      let value: boolean | null;
      switch (keyword) {
        case 'false':
        case 'true':
          tokenType = TokenType.Boolean;
          value = keyword === 'true' ? true : false;
          break;
        case 'null':
          tokenType = TokenType.Null;
          value = null;
          break;
        default:
          throw new Error(`Unknown keyword: ${keyword}`);
      }
      context.tokens.push({
        type: tokenType,
        value,
      });
      context.index = index + i;
    }
    */

    internal fun parseKeyword(context: ParseContext, keyword: Keyword) {
        val (source, index) = context
        var i = 0
        while (!isIndexEnd(context, i) && i < keyword.length) {
            if (index + i >= source.length) {
                context.index = index + i
                return
            }
            if (source[index + i] != keyword[i]) {
                throw SyntaxException("Failed to parse value at position: $index")
            }
            if (i == keyword.length - 1) {
                break
            } else {
                i++
            }
        }
        val tokenType: TokenType
        val value: Any?
        when (keyword) {
            "false", "true" -> {
                tokenType = TokenType.Boolean
                value = keyword == "true"
            }

            "null" -> {
                tokenType = TokenType.Null
                value = null
            }

            else -> {
                throw JsonParseException("Unknown keyword: $keyword")
            }
        }
        context.tokens.add(Token.from(tokenType, value))
        context.index = index + i
    }

    /*
    export function tokenize(str: string) {
      const context: ParseContext = { index: 0, source: str, tokens: [] };
      while (!isEnd(context)) {
        if (isNumberStart(context)) {
          parseNumber(context);
        } else if (isStringrStart(context)) {
          parseString(context);
        } else {
          const char = context.source[context.index];
          if (isWhitespace(char)) {
            skipSpaces(context);
            continue;
          }
          switch (char) {
            case '[': {
              context.tokens.push({
                type: TokenType.LeftBracket,
              });
              break;
            }
            case ']': {
              context.tokens.push({
                type: TokenType.RightBracket,
              });
              break;
            }
            case '{': {
              context.tokens.push({
                type: TokenType.LeftBrace,
              });
              break;
            }
            case '}': {
              context.tokens.push({
                type: TokenType.RightBrace,
              });
              break;
            }
            case ',': {
              context.tokens.push({
                type: TokenType.Comma,
              });
              break;
            }
            case ':': {
              context.tokens.push({
                type: TokenType.Colon,
              });
              break;
            }
            case 't': {
              parseKeyword(context, 'true');
              break;
            }
            case 'f': {
              parseKeyword(context, 'false');
              break;
            }
            case 'n': {
              parseKeyword(context, 'null');
              break;
            }
            default: {
              throw new SyntaxError(
                `Unexpected character: "${char}" ` + `at position: ${context.index}`
              );
            }
          }
          context.index++;
        }
      }

  return context.tokens;
}
 */

    fun tokenize(str: String): MutableList<Token> {
        val context = ParseContext(source = str, index = 0, tokens = mutableListOf())
        while (!isEnd(context)) {
            if (isNumberStart(context)) {
                parseNumber(context)
            } else if (isStringrStart(context)) {
                parseString(context)
            } else {
                val char = context.source[context.index]
                if (char.isWhitespace()) {
                    skipSpaces(context)
                    continue
                }
                when (char) {
                    '[' -> {
                        context.tokens.add(Token.from(TokenType.LeftBracket))
                    }

                    ']' -> {
                        context.tokens.add(Token.from(TokenType.RightBracket))
                    }

                    '{' -> {
                        context.tokens.add(Token.from(TokenType.LeftBrace))
                    }

                    '}' -> {
                        context.tokens.add(Token.from(TokenType.RightBrace))
                    }

                    ',' -> {
                        context.tokens.add(Token.from(TokenType.Comma))
                    }

                    ':' -> {
                        context.tokens.add(Token.from(TokenType.Colon))
                    }

                    't' -> {
                        parseKeyword(context, "true")
                    }

                    'f' -> {
                        parseKeyword(context, "false")
                    }

                    'n' -> {
                        parseKeyword(context, "null")
                    }

                    else -> {
                        throw SyntaxException("Unexpected character: \"$char\" at position: ${context.index}")
                    }
                }
                context.index++
            }
        }

        return context.tokens
    }
}