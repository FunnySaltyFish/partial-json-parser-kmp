package com.funnysaltyfish.partialjsonparser

import kotlin.test.Test
import kotlin.test.assertFailsWith

// test parse
class ParseTest {
    private val completeJsonList = arrayOf(
        // complete
        "{\"key\":\"text\"}",
        "{\"key\":\"text\", \"key2\":\"text2\"}",

        // num, null, bool
        "{\"key\":123}",
        "{\"key\":123.456}",
        "{\"key\":-123.34}",
        "{\"key\":-1.2e5}",
        "{\"key\":null}",
        "{\"key\":true, \"key2\":false}",

        // array
        "{\"key\":[]}",
        "{\"key\":[1,\"2\",3.4]}",
        "{\"k\":[[1,2],[\"v\"]]}",
    )

    private val partialJsonList = arrayOf(
        // basic text
        "{\"ke",
        "{\"key\"",
        "{\"key\":",
        "{\"key\":\"te",
        "{\"key\":\"text\", \"key2",
        "{\"key\":\"text\", \"key2\":\"t}",

        // num, null, bool
        "{\"k\":1.",
        "{\"k\":true",
        "{\"k\":false",
        "{\"k\":null",
        "{\"k\":-1.2e",

        // incomplete keywords
        "{\"k\":tru",
        "{\"k\":fals",
        "{\"k\":nul",

        // array
        "{\"k\":[1,2,3",
        "{\"k\":[[1,2",
        "{\"k\":[[1,2],[\"v\"",

        // nesting
        "{\"k\":{\"k2\":1",
        "{\"k\":{\"k2\":1, \"k3\":2",
        "{\"k\":{\"k2\":1, \"k3\":",
        "{\"k\":[{\"k2\":1, \"k3\":2",
        "{\"k\":[{\"k2\":1, \"k3\":2.",
    )

    // These json cannot be parsed and will throw Exception
    private val errorJson = arrayOf(
        "{\"key\":tlue",
        "{\"key\":felse",
        "{\"key\":nill",
    )

//    @Test
//    fun test_one() {
//        val json = "{\"key\":-1.2e" // value of exponent is incomplete
//        val result = PartialJsonParser.parse(json)
//        println(result)
//    }


    @Test
    fun test_parseCompleteJson() {
        testParse(completeJsonList)
    }

    @Test
    fun test_parsePartialJson() {
        testParse(partialJsonList)
    }

    @Test
    fun test_parseErrorJson() {
        for (json in errorJson) {
            assertFailsWith(SyntaxException::class) {
                PartialJsonParser.parse(json)
            }
        }
    }

    private fun testParse(jsonList: Array<String>) {
        val indent = jsonList.maxOf { it.length }
        for (json in jsonList) {
            val result = PartialJsonParser.parse(json)
            println(
                buildString {
                    append(json)
                    append(" ".repeat(indent - json.length))
                    append(" -> ")
                    append(result)
                }
            )

        }
    }
}