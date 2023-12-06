package com.funnysaltyfish.partialjsonparser.tokenizer

import com.funnysaltyfish.partialjsonparser.ParseContext

/**
 * import { ParseContext } from '../types';
 *
 * export function isEnd(context: ParseContext) {
 *   return context.index >= context.source.length;
 * }
 *
 * export function isIndexEnd(context: ParseContext, index: number) {
 *   return index >= context.source.length;
 * }
 *
 */

internal fun isEnd(context: ParseContext): Boolean {
    return context.index >= context.source.length
}

internal fun isIndexEnd(context: ParseContext, index: Int): Boolean {
    return index >= context.source.length
}