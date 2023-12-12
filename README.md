# Partial-JSON-Paser-KMP

[![Kotlin Version](https://img.shields.io/badge/Kotlin-1.9.0-B125EA?logo=kotlin)](https://kotlinlang.org)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.funnysaltyfish/partial-json-parser.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/de.peilicke.sascha/kase64)
[![License](http://img.shields.io/:License-Apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

![badge-android](http://img.shields.io/badge/Platform-Android-brightgreen.svg?logo=android)
![badge-ios](http://img.shields.io/badge/Platform-iOS-orange.svg?logo=apple)
![badge-js](http://img.shields.io/badge/Platform-NodeJS-yellow.svg?logo=javascript)
![badge-jvm](http://img.shields.io/badge/Platform-JVM-red.svg?logo=openjdk)
![badge-linux](http://img.shields.io/badge/Platform-Linux-lightgrey.svg?logo=linux)

[中文](README_CN.md)

## Introduction
This library helps to parse partial JSON (that is, incomplete JSON) in Kotlin. It is implemented in **pure Kotlin** so that can be used in KMP project.

## Usage
```kotlin
import com.funnysaltyfish.partialjsonparser.PartialJsonParser

val partialJson = "{\"key\":\"Hello, "
val map = PartialJsonParser.parse(partialJson) as? Map<*, *> // Map(key=Hello, )
println(map) // {key=Hello, }
println(map?.get("key")) // Hello,
```

The method will throw `JsonParseException` if the JSON is invalid. In some extreme cases, it might also throw `IndexOutOfBoundsException`.

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
implementation("io.github.funnysaltyfish:partial-json-parser:1.0.2")
```

## Examples
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

To see more examples, please run `ParseTest.kt`.

## Origin Source
Interestingly, the code is converted from the TypeScript library [here](https://github.com/SimonTart/json-fragment-parser) by GitHub Copilot, I make it suitable for Kotlin style, modify some extreme cases, write some tests and publish it to Maven Central. Thanks for it.