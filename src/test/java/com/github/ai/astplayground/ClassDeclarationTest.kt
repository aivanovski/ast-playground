package com.github.ai.astplayground

import com.github.ai.astplayground.assertionDsl.AstBuilderDsl.buildAst
import org.junit.jupiter.api.Test

class ClassDeclarationTest {

    @Test
    fun `should support class declaration`() {
        parseAndAssert(
            input = """
                class Test {
                }
            """,
            expected = buildAst {
                `class`("Test")
            }
        )
    }
}