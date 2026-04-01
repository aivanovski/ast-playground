package com.github.ai.astplayground.transpiler.serializer

interface AstSerializer<T> {
    fun serialize(nodes: List<T>): String
}