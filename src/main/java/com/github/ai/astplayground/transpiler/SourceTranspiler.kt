package com.github.ai.astplayground.transpiler

interface SourceTranspiler {
    fun transpile(input: String): String
}