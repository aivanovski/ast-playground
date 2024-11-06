package com.github.ai.astplayground

import com.github.ai.astplayground.transpiler.DeepJavaToKotlinTranspiler
import com.github.ai.astplayground.transpiler.SourceTranspiler
import io.kotest.matchers.shouldBe

fun transpileAndAssert(
    input: String,
    output: String,
    isTrimIndents: Boolean = true,
    isFilterEmptyLines: Boolean = true,
    transpiler: SourceTranspiler = DeepJavaToKotlinTranspiler()
) {
    val processedInput = process(input, isTrimIndents, isFilterEmptyLines)
    val processedOutput = process(output, isTrimIndents, isFilterEmptyLines)

    val result = transpiler.transpile(processedInput)

    println("INPUT:")
    println(processedInput)

    println("RESULT:")
    println(result)

    println("EXPECTED:")
    println(processedOutput)

    result.trim() shouldBe processedOutput
}

private fun process(
    input: String,
    isTrimIndents: Boolean = true,
    isFilterEmptyLines: Boolean = true,
): String {
    val trimmed = if (isTrimIndents) input.trimIndent() else input

    return if (isFilterEmptyLines) {
        trimmed.lines()
            .filter { line -> line.isNotBlank() }
            .joinToString(separator = "\n")
    } else {
        trimmed
    }
}