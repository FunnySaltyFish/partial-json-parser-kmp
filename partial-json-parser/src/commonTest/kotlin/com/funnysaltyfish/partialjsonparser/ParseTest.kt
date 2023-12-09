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

    private val testJson = """{
    "字符串": "Hello, World!",
    "整数": 123,
    "浮点数": 3.14,
    "指数": 3.14e6,
    "布尔值": true,
    "空值": null,
    "对象": {
        "属性1": "值1",
        "属性2": "值2",
        "带有换行符": "值1\n值2",
        "带有unicode": "值1\u0020值2\u0020值3",
        "嵌套引号": "它说:\"我不知道\"",
    },
    "数组": [1, 2, 3, 4],
    "嵌套对象数组": [
        {
          "姓名": "张三",
          "年龄": 30
        },
        {
          "姓名": "李四",
          "年龄": 25
        }
    ]
}"""

    @Test
    fun test_one() {
        val partialJson = "{\"key\":\"Hello, "
        val map = PartialJsonParser.parse(partialJson) as? Map<*, *> // Map(key=Hello, )
        println(map) // {key=Hello, }
        println(map?.get("key")) // Hello,
    }


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

    @Test
    fun test_parseStreamJson() {
        var i = 1
        var currentPart = "{"
        while (i < testJson.length) {
            if (testJson[i].isWhitespace()) {
                i++
                continue
            }
            val char = testJson[i]
            currentPart += char
            try {
                println(PartialJsonParser.parse(currentPart))
            } catch (e: JsonParseException) {
                //
            }
            i += 1
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