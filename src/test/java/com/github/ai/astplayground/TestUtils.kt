package com.github.ai.astplayground

import com.github.ai.astplayground.transpiler.parser.JDKAstParser
import com.github.ai.astplayground.transpiler.model.JavaAstNode
import com.github.ai.astplayground.transpiler.serializer.KotlinSerializer
import io.kotest.matchers.shouldBe
import kotlin.text.split

fun parseAndAssert(
    input: String,
    expected: List<JavaAstNode>
) {
    val parseResult = JDKAstParser().parseToAst(input)
    parseResult shouldBe expected
}

fun serializeAndAssert(
    input: List<JavaAstNode>,
    expected: String
) {
    val trimmed = expected
        .split("\n")
        .map { line -> line.trim() }
        .filter { line -> line.isNotEmpty() }
        .joinToString(separator = "\n")

    val serializationResult = KotlinSerializer().serialize(input)
    serializationResult shouldBe trimmed
}