package com.github.ai.astplayground.parser

import com.github.ai.astplayground.astDsl.AstBuilderDsl.javaAst
import com.github.ai.astplayground.parseAndAssert
import org.junit.jupiter.api.Test

class PackageTest {

    @Test
    fun `should support package declaration`() {
        parseAndAssert(
            input = """
                package com.example.test;
            """,
            expected = javaAst {
                `package`("com.example.test")
            }
        )
    }

    @Test
    fun `should support default package`() {
        parseAndAssert(
            input = "",
            expected = javaAst {

            }
        )
    }
}