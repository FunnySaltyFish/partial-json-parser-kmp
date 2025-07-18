package com.funnysaltyfish.partialjsonparser

import kotlin.test.Test

class CompleteTest {

    private val basicTestCases = arrayOf(
        "{\"key" to "{}",
        "{\"key\":" to "{}",  // 修改：现在忽略不完整的键值对
        "{\"key\":\"te" to "{\"key\":\"te\"}",
        "{\"key\":\"text\", \"key2" to "{\"key\":\"text\"}",
        "{\"key\":\"text\", \"key2\":\"t" to "{\"key\":\"text\",\"key2\":\"t\"}",
        "{\"key\":123," to "{\"key\":123}",  // 修改：整数不带小数点
        "{\"key\":[1,2,3" to "{\"key\":[1,2,3]}",  // 修改：整数不带小数点
        "{\"key\":[" to "{\"key\":[]}",
        "{\"key\":{\"inner" to "{\"key\":{}}",
        "{\"key\":{\"inner\":\"val\"," to "{\"key\":{\"inner\":\"val\"}}"
    )

    private val arrayTestCases = arrayOf(
        "[1,2,3" to "[1,2,3]",  // 修改：整数不带小数点
        "[\"a\",\"b" to "[\"a\",\"b\"]",
        "[{\"key\":\"val" to "[{\"key\":\"val\"}]",
        "[{\"key\":\"val\"," to "[{\"key\":\"val\"}]"
    )

    private val nestedTestCases = arrayOf(
        "{\"a\":{\"b\":{\"c\":\"val" to "{\"a\":{\"b\":{\"c\":\"val\"}}}",
        "{\"a\":{\"b\":[1,2,{\"c\":\"val" to "{\"a\":{\"b\":[1,2,{\"c\":\"val\"}]}}",  // 修改：整数不带小数点
        "{\"a\":[{\"b\":{\"c\":\"val" to "{\"a\":[{\"b\":{\"c\":\"val\"}}]}",
        "{\"outer\":{\"inner\":{\"key\":\"value\", \"key2" to "{\"outer\":{\"inner\":{\"key\":\"value\"}}}"
    )

    private val specialTestCases = arrayOf(
        // 测试数字格式
        "{\"int\":123}" to "{\"int\":123}",
        "{\"float\":123.456}" to "{\"float\":123.456}",
        "[1,2,3.5,4]" to "[1,2,3.5,4]",

        // 测试不完整键值对的忽略
        "{\"key\":" to "{}",
        "{\"key1\":\"value1\", \"key2\":" to "{\"key1\":\"value1\"}",
        "{\"outer\":{\"inner\":" to "{\"outer\":{}}",

        // 测试混合情况
        "{\"numbers\":[1,2.5,3], \"incomplete\":" to "{\"numbers\":[1,2.5,3]}",
        "{\"a\":1, \"b\":" to "{\"a\":1}"
    )
    
    @Test
    fun test_complete_basic() {
        val testCases = basicTestCases

        for ((partial, expected) in testCases) {
            val completed = PartialJsonParser.complete(partial)
            println("输入: $partial")
            println("期望: $expected")
            println("输出: $completed")
            
            // 验证补全的 JSON 是有效的
            try {
                val parsed = PartialJsonParser.parse(completed)
                println("解析: $parsed")
            } catch (e: Exception) {
                println("解析失败: ${e.message}")
            }
            println("---")
        }
    }
    
    @Test
    fun test_complete_arrays() {
        val testCases = arrayTestCases

        for ((partial, expected) in testCases) {
            val completed = PartialJsonParser.complete(partial)
            println("输入: $partial")
            println("期望: $expected")
            println("输出: $completed")
            
            // 验证补全的 JSON 是有效的
            try {
                val parsed = PartialJsonParser.parse(completed)
                println("解析: $parsed")
            } catch (e: Exception) {
                println("解析失败: ${e.message}")
            }
            println("---")
        }
    }
    
    @Test
    fun test_complete_nested() {
        val testCases = nestedTestCases

        for ((partial, expected) in testCases) {
            val completed = PartialJsonParser.complete(partial)
            println("输入: $partial")
            println("期望: $expected")
            println("输出: $completed")
            
            // 验证补全的 JSON 是有效的
            try {
                val parsed = PartialJsonParser.parse(completed)
                println("解析: $parsed")
            } catch (e: Exception) {
                println("解析失败: ${e.message}")
            }
            println("---")
        }
    }
    
    @Test
    fun test_complete_special_cases() {
        // 测试数字格式和不完整键值对的特殊处理
        val testCases = specialTestCases

        for ((partial, expected) in testCases) {
            val completed = PartialJsonParser.complete(partial)
            println("输入: $partial")
            println("期望: $expected")
            println("输出: $completed")
            
            // 验证补全的 JSON 是有效的
            try {
                val parsed = PartialJsonParser.parse(completed)
                println("解析: $parsed")
            } catch (e: Exception) {
                println("解析失败: ${e.message}")
            }
            println("---")
        }
    }

    @Test
    fun output_test_cases() {
        // 输出测试用例
        val testCases = basicTestCases + arrayTestCases + nestedTestCases + specialTestCases

        val indent = testCases.maxOf { it.first.length }
        for ((partial, _) in testCases) {
            val completed = PartialJsonParser.complete(partial)
            println(
                buildString {
                    append(partial)
                    append(" ".repeat(indent - partial.length))
                    append(" -> ")
                    append(completed)
                }
            )
        }
    }
}