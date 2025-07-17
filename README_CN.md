# Partial-JSON-Paser-KMP

[![Kotlin Version](https://img.shields.io/badge/Kotlin-2.2.0-B125EA?logo=kotlin)](https://kotlinlang.org)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.funnysaltyfish/partial-json-parser.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/de.peilicke.sascha/kase64)
[![License](http://img.shields.io/:License-Apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

![badge-android](http://img.shields.io/badge/Platform-Android-brightgreen.svg?logo=android)
![badge-ios](http://img.shields.io/badge/Platform-iOS-orange.svg?logo=apple)
![badge-js](http://img.shields.io/badge/Platform-NodeJS-yellow.svg?logo=javascript)
![badge-jvm](http://img.shields.io/badge/Platform-JVM-red.svg?logo=openjdk)
![badge-linux](http://img.shields.io/badge/Platform-Linux-lightgrey.svg?logo=linux)

## 简介

这个库帮助在 Kotlin 中解析部分JSON（即不完整的JSON，比如由类似 ChatGPT 的大语言模型在流式返回中生成的）。它是用 **纯 Kotlin** 实现的，可在 KMP 项目中使用。

## 用法

使用起来就一行：

```kotlin
import com.funnysaltyfish.partialjsonparser.PartialJsonParser

val partialJson = "{\"key\":\"Hello, "
val map = PartialJsonParser.parse(partialJson) as? Map<*, *> // Map(key=Hello, )
println(map) // {key=Hello, }
println(map?.get("key")) // Hello,
```

如果 JSON 无效，则该方法将抛出 `JsonParseException`。在某些极端情况下，它也可能抛出 `IndexOutOfBoundsException`
（理论上说不会出现这种情况，如果有欢迎 issue 和 PR）。

实际上，`parse` 只是 `tokenize` 和 `parseTokens` 的组合，你也可以分开用：

```kotlin
fun parse(str: String): Any? {
    val tokens = Tokenizer.tokenize(str)
    return Parser.parseTokens(tokens)
}
```

## 实现

该库已经发布到 Maven Central，通过下面的方式引入：

```groovy
implementation("io.github.funnysaltyfish:partial-json-parser:1.0.3")
```

## 示例

以下是一些示例：（原始JSON字符串 -> 解析后的map）
```
{"ke                      -> {}
{"key"                    -> {}
{"key":                   -> {}
{"key":"te                -> {key=te}
{"key":"text", "key2      -> {key=text}
{"key":"text", "key2":"t} -> {key=text, key2=t}}

{"k":[1,2,3               -> {k=[1.0, 2.0, 3.0]}
{"k":[[1,2                -> {k=[[1.0, 2.0]]}
{"k":[[1,2],["v"          -> {k=[[1.0, 2.0], [v]]}
{"k":{"k2":1              -> {k={k2=1.0}}
{"k":{"k2":1, "k3":2      -> {k={k2=1.0, k3=2.0}}
{"k":{"k2":1, "k3":       -> {k={k2=1.0}}
{"k":[{"k2":1, "k3":2     -> {k=[{k2=1.0, k3=2.0}]}
```

总的原则是，我们会尽可能多的解析出信息。你可以运行 ParseTest.kt 文件来查看更多情况

## 特别鸣谢
这个库的代码是从 [这个 TypeScript 库](https://github.com/SimonTart/json-fragment-parser) 来的，用的 GitHub Copilot 一点点转换的, 我将它的代码略作调整以符合 Kotlin 风格、优化了一些极端情况、写了测试以及完成了发布。由衷的感谢一下！