package com.funnysaltyfish.partialjsonparser.tokenizer

import com.funnysaltyfish.partialjsonparser.ParseContext
import com.funnysaltyfish.partialjsonparser.Token

/**
 * import { TokenType } from '../constant';
 * import { ParseContext } from '../types';
 * import { isIndexEnd } from './utils';
 *
 * export function isNumberStart(context: ParseContext): boolean {
 *   const char = context.source[context.index];
 *   return Boolean(char.match(/^(-|\d)$/));
 * }
 *
 */
private const val NUM_START_CHARS = "-0123456789"
internal fun isNumberStart(context: ParseContext): Boolean {
    val char = context.source[context.index]
    return char in NUM_START_CHARS
}

 /*
 * export function parsesNegative(context: ParseContext) {
 *   const { index, source } = context;
 *   let i = index;
 *   let isNegative = false;
 *   if (source[i] === '-') {
 *     isNegative = true;
 *     i++;
 *   }
 *   context.index = i;
 *   return isNegative;
 * }
 */

internal fun parsesNegative(context: ParseContext): Boolean {
    val (source, index) = context
    var i = index
    var isNegative = false
    if (source[i] == '-') {
        isNegative = true
        i++
    }
    context.index = i
    return isNegative
}


 /* export function parseInteger(context: ParseContext) {
 *   const { index, source } = context;
 *   let integer = '';
 *   let i = index;
 *   while (!isIndexEnd(context, i)) {
 *     if (source[i].match(/^\d$/)) {
 *       integer += source[i]!;
 *       i++;
 *     } else {
 *       break;
 *     }
 *   }
 *   context.index = i;
 *   return integer;
 * }
 */

internal fun parseInteger(context: ParseContext): String {
    val (source, index) = context
    var integer = ""
    var i = index
    while (!isIndexEnd(context, i)) {
        if (source[i].isDigit()) {
            integer += source[i]
            i++
        } else {
            break
        }
    }
    context.index = i
    return integer
}


/* export function parseFraction(context: ParseContext) {
 *   const { index, source } = context;
 *   let fraction = '';
 *   let i = index;
 *   if (!isIndexEnd(context, i) && source[i] === '.') {
 *     i++;
 *     while (!isIndexEnd(context, i)) {
 *       if (source[i].match(/^\d$/)) {
 *         fraction += source[i]!;
 *         i++;
 *       } else {
 *         break;
 *       }
 *     }
 *   }
 *
 *   context.index = i;
 *   return fraction;
 * }
 */

internal fun parseFraction(context: ParseContext): String {
    val (source, index) = context
    var fraction = ""
    var i = index
    if (!isIndexEnd(context, i) && source[i] == '.') {
        i++
        while (!isIndexEnd(context, i)) {
            if (source[i].isDigit()) {
                fraction += source[i]
                i++
            } else {
                break
            }
        }
    }

    context.index = i
    return fraction
}

 /*
 * export function parseExponent(context: ParseContext) {
 *   const { index, source } = context;
 *   let i = index;
 *   let isExponentNegative = false;
 *   let exponent = '';
 *   if (!!isIndexEnd(context, i) && ['e', 'E'].includes(source[i]!)) {
 *     i++;
 *
 *     if (source[i] === '+') {
 *       i++;
 *     } else if (source[i] === '-') {
 *       isExponentNegative = true;
 *       i++;
 *     }
 *
 *     while (!isIndexEnd(context, i)) {
 *       if (source[i]!.match(/^\d$/)) {
 *         exponent += source[i]!;
 *         i++;
 *       } else {
 *         break;
 *       }
 *     }
 *   }
 *   context.index = i;
 *   return {
 *     isExponentNegative,
 *     exponent,
 *   };
 * }
 */

internal fun parseExponent(context: ParseContext): Pair<Boolean, String> {
    val (source, index) = context
    var i = index
    var isExponentNegative = false
    var exponent = ""
    if (!isIndexEnd(context, i) && source[i] in "eE") {
        i++

        // extreme case: exponent is the last character, like 1.2e
        if (i >= source.length) {
            context.index = i
            return false to ""
        }

        if (source[i] == '+') {
            i++
        } else if (source[i] == '-') {
            isExponentNegative = true
            i++
        }

        while (!isIndexEnd(context, i)) {
            if (source[i].isDigit()) {
                exponent += source[i]
                i++
            } else {
                break
            }
        }
    }
    context.index = i
    return isExponentNegative to exponent
}

 /* export function parseNumber(context: ParseContext) {
 *   const isNegative = parsesNegative(context);
 *   const integer = parseInteger(context);
 *   const fraction = parseFraction(context);
 *   const { isExponentNegative, exponent } = parseExponent(context);
 *   if (!integer) {
 *     return;
 *   }
 *   let value = Number(
 *     (isNegative ? '-' : '') +
 *       integer +
 *       (fraction ? `.${fraction}` : '') +
 *       (exponent ? `e${isExponentNegative ? '-' : ''}${exponent}` : '')
 *   );
 *   context.tokens.push({
 *     type: TokenType.Number,
 *     value,
 *   });
 * }
 *
 */

internal fun parseNumber(context: ParseContext) {
    val isNegative = parsesNegative(context)
    val integer = parseInteger(context)
    val fraction = parseFraction(context)
    val (isExponentNegative, exponent) = parseExponent(context)
    if (integer.isEmpty()) {
        return
    }
    val value = (if (isNegative) "-" else "") +
            integer +
            (if (fraction.isNotEmpty()) ".$fraction" else "") +
            (if (exponent.isNotEmpty()) "e${if (isExponentNegative) "-" else ""}$exponent" else "")
    context.tokens += Token.NumberToken(value.toDouble())
}