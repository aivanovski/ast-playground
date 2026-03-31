package com.github.ai.astplayground

import com.github.ai.astplayground.transpiler.parser.JDKAstParser
import com.github.ai.astplayground.transpiler.model.JavaAstNode
import io.kotest.matchers.shouldBe

fun parseAndAssert(
    input: String,
    expected: List<JavaAstNode>
) {
    val ast = JDKAstParser().parseToAst(input)
    ast shouldBe expected
}