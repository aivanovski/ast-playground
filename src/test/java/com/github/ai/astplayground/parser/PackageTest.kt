package com.github.ai.astplayground.parser

import com.github.ai.astplayground.astDsl.AstBuilderDsl.buildAst
import com.github.ai.astplayground.parseAndAssert
import org.junit.jupiter.api.Test

class PackageTest {

    @Test
    fun `should support package declaration`() {
        parseAndAssert(
            input = """
                package com.example.test;
            """,
            expected = buildAst {
                `package`("com.example.test")
            }
        )
    }

    @Test
    fun `should support default package`() {
        parseAndAssert(
            input = "",
            expected = buildAst {

            }
        )
    }
}