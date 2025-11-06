# Partial-JSON-Paser-KMP

[![Kotlin Version](https://img.shields.io/badge/Kotlin-2.2.0-B125EA?logo=kotlin)](https://kotlinlang.org)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.funnysaltyfish/partial-json-parser.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.funnysaltyfish/partial-json-parser)
[![License](http://img.shields.io/:License-MIT-blue.svg)](https://opensource.org/license/MIT/)

![badge-android](http://img.shields.io/badge/Platform-Android-brightgreen.svg?logo=android)
![badge-ios](http://img.shields.io/badge/Platform-iOS-orange.svg?logo=apple)
![badge-js](http://img.shields.io/badge/Platform-NodeJS-yellow.svg?logo=javascript)
![badge-jvm](http://img.shields.io/badge/Platform-JVM-red.svg?logo=openjdk)
![badge-linux](http://img.shields.io/badge/Platform-Linux-lightgrey.svg?logo=linux)

[中文](README_CN.md)

## Introduction
This library helps to **parse and repair partial JSON** (that is, incomplete JSON) in Kotlin. Perfect for handling streaming JSON from LLMs like ChatGPT. It is implemented in **pure Kotlin** so that can be used in KMP project.

## Features
- **Parse** incomplete JSON strings into usable objects
- **Complete/Repair** partial JSON to valid JSON format
- Handle streaming JSON data in real-time
- Pure Kotlin implementation for KMP compatibility
- Robust error handling for malformed JSON

## Usage

### Parse Partial JSON
```kotlin
import com.funnysaltyfish.partialjsonparser.PartialJsonParser

val partialJson = "{\"key\":\"Hello, "
val map = PartialJsonParser.parse(partialJson) as? Map<*, *> // Map(key=Hello, )
println(map) // {key=Hello, }
println(map?.get("key")) // Hello,
```

### Complete/Repair Partial JSON
```kotlin
import com.funnysaltyfish.partialjsonparser.PartialJsonParser

val partialJson = "{\"key\":\"Hello, "
val completedJson = PartialJsonParser.complete(partialJson)
println(completedJson) // {"key":"Hello, "}

// More examples
val incomplete = "{\"name\":\"John\", \"age\":"
val repaired = PartialJsonParser.complete(incomplete)
println(repaired) // {"name":"John"}
```

The `parse` method will throw `JsonParseException` if the JSON is invalid. The `complete` method repairs partial JSON into valid JSON format by removing incomplete elements.

(In some extreme cases, it might also throw `IndexOutOfBoundsException`, which should not occur although. If that did happen, feel free to open an issue with your sample.)


Actually, `parse` is just a combination of `tokenize` and `parseTokens`, you can use them separately if you want

```kotlin
fun parse(str: String): Any? {
    val tokens = Tokenizer.tokenize(str)
    return Parser.parseTokens(tokens)
}
```

## Implementation
The library is published to Maven Central.

```groovy
implementation("io.github.funnysaltyfish:partial-json-parser:1.0.3")
```

## Examples

### Parse Examples
Below are some examples: (originJsonString -> parsed map)
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

### Complete/Repair Examples
Below are examples of JSON completion/repair: (partial JSON -> completed JSON)
```
{"key                                   -> {}
{"key":                                 -> {}
{"key":"te                              -> {"key":"te"}
{"key":"text", "key2                    -> {"key":"text"}
{"key":"text", "key2":"t                -> {"key":"text","key2":"t"}
{"key":123,                             -> {"key":123}
{"key":[1,2,3                           -> {"key":[1,2,3]}
{"key":[                                -> {"key":[]}
{"key":{"inner                          -> {"key":{}}
{"key":{"inner":"val",                  -> {"key":{"inner":"val"}}
[1,2,3                                  -> [1,2,3]
["a","b                                 -> ["a","b"]
[{"key":"val                            -> [{"key":"val"}]
[{"key":"val",                          -> [{"key":"val"}]
{"a":{"b":{"c":"val                     -> {"a":{"b":{"c":"val"}}}
{"a":{"b":[1,2,{"c":"val                -> {"a":{"b":[1,2,{"c":"val"}]}}
{"a":[{"b":{"c":"val                    -> {"a":[{"b":{"c":"val"}}]}
{"outer":{"inner":{"key":"value", "key2 -> {"outer":{"inner":{"key":"value"}}}
{"numbers":[1,2.5,3], "incomplete":     -> {"numbers":[1,2.5,3]}
{"a":1, "b":                            -> {"a":1}
```

To see more examples, please run `ParseTest.kt` and `CompleteTest.kt`.

## Origin Source
Interestingly, the code is converted from the TypeScript library [here](https://github.com/SimonTart/json-fragment-parser) by GitHub Copilot, I make it suitable for Kotlin style, modify some extreme cases, write some tests and publish it to Maven Central. Thanks for it.
