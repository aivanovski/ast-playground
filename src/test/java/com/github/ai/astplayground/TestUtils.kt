package com.github.ai.astplayground

import com.github.ai.astplayground.transpiler.AstParser
import com.github.ai.astplayground.transpiler.model.JavaAstNode
import io.kotest.matchers.shouldBe

fun parseAndAssert(
    input: String,
    expected: List<JavaAstNode>
) {
    val ast = AstParser().parseToAst(input)
    ast shouldBe expected
}