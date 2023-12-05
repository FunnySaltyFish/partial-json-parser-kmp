package com.funnysaltyfish.partialjsonparser.tokenizer

import com.funnysaltyfish.partialjsonparser.ParseContext
import com.funnysaltyfish.partialjsonparser.Token

/*
import { ParseContext } from '../types';
import { isIndexEnd } from './utils';
import { TokenType } from '../constant';

export function isStringrStart(context: ParseContext): boolean {
  const char = context.source[context.index];
  return char === '"';
}
*/

internal fun isStringrStart(context: ParseContext): Boolean {
    val char = context.source[context.index]
    return char == '"'
}

/*
const codePoints: Record<strang, string> = {
  '\\"': '"',
  '\\\\': '\\',
  '\\/': '/',
  '\\b': '\b',
  '\\f': '\f',
  '\\n': '\n',
  '\\r': '\r',
  '\\t': '\t',
};
*/
private val codePoints = mapOf(
    "\\\"" to "\"",
    "\\\\" to "\\",
    "\\/" to "/",
    "\\b" to "\b",
    "\\f" to "\u000C",
    "\\n" to "\n",
    "\\r" to "\r",
    "\\t" to "\t"
)


/*
export function parseString(context: ParseContext) {
  const { source, index } = context;

  let value = '';

  let i = index + 1;

  while (!isIndexEnd(context, i)) {
    const char = source[i] as string;

    if (char === '\\') {
      const twoChars = source.substring(i, i + 2);
      const codepoint = codePoints[twoChars];

      if (codepoint) {
        value += codepoint;
        i += 2;
      } else if (twoChars === '\\u') {
        const charHex = source.substring(i + 2, i + 6);
        value += String.fromCharCode(parseInt(charHex, 16));
        i += 6;
      } else {
        // 可能这个时候就结束了，所以先不管，跳过去吧
        i++;
        console.error(`Unknown escape sequence: "${twoChars}"`);
        // throw new SyntaxError(`Unknown escape sequence: "${twoChars}"`);
      }
    } else if (char === '"') {
      // End of string
      i++;
      break;
    } else {
      value += char;
      i++;
    }
  }
  context.index = i;
  context.tokens.push({ type: TokenType.String, value });
}
 */

internal fun parseString(context: ParseContext) {
    val (source, index) = context
    var value = ""
    var i = index + 1

    while (!isIndexEnd(context, i)) {
        val char = source[i]

        if (char == '\\') {
            val twoChars = source.substring(i, i + 2)
            val codepoint = codePoints[twoChars]

            if (codepoint != null) {
                value += codepoint
                i += 2
            } else if (twoChars == "\\u") {
                val charHex = source.substring(i + 2, i + 6)
                value += charHex.toInt(16).toChar()
                i += 6
            } else {
                // 可能这个时候就结束了，所以先不管，跳过去吧
                i++
                println("Unknown escape sequence: \"$twoChars\"")
                // throw new SyntaxError(`Unknown escape sequence: "${twoChars}"`);
            }
        } else if (char == '"') {
            // End of string
            i++
            break
        } else {
            value += char
            i++
        }
    }
    context.index = i
    context.tokens.add(Token.StringToken(value))
}