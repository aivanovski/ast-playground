package com.github.ai.astplayground.parser

import com.github.ai.astplayground.astDsl.AstBuilderDsl.javaAst
import com.github.ai.astplayground.parseAndAssert
import org.junit.jupiter.api.Test

class ClassDeclarationTest {

    @Test
    fun `should support class declaration`() {
        parseAndAssert(
            input = """
                class Test {
                }
            """,
            expected = javaAst {
                `class`("Test")
            }
        )
    }
}