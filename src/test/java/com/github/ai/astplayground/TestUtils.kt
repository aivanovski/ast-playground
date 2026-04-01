package com.github.ai.astplayground

import com.github.ai.astplayground.transpiler.parser.JDKAstParser
import com.github.ai.astplayground.transpiler.parser.model.JavaAstNode
import com.github.ai.astplayground.transpiler.serializer.KotlinSerializer
import com.github.ai.astplayground.transpiler.transformer.JavaToKotlinTransformer
import io.kotest.matchers.shouldBe
import kotlin.text.split

fun parseAndAssert(
    input: String,
    expected: List<JavaAstNode>
) {
    val parseResult = JDKAstParser().parseToAst(input)
    parseResult shouldBe expected
}

fun transpileAndAssert(
    input: List<JavaAstNode>,
    expected: String
) {
    val kotlinAst = JavaToKotlinTransformer().transform(input)
    val serializationResult = KotlinSerializer().serialize(kotlinAst)
    serializationResult shouldBe expected.trimCode()
}

fun transpileAndAssert(
    input: String,
    expected: String
) {
    val javaAst = JDKAstParser().parseToAst(input)

    val kotlinAst = JavaToKotlinTransformer().transform(javaAst)
    val result = KotlinSerializer().serialize(kotlinAst)

    result shouldBe expected.trimCode()
}

private fun String.trimCode(): String {
    return this.split("\n")
        .map { line -> line.trim() }
        .filter { line -> line.isNotEmpty() }
        .joinToString(separator = "\n")
}