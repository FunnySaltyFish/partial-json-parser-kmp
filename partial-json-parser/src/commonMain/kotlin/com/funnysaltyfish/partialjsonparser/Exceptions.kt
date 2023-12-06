package com.funnysaltyfish.partialjsonparser


open class JsonParseException(override val message: String): Exception(message)

class SyntaxException(override val message: String): JsonParseException(message)
