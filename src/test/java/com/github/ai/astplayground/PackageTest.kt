package com.github.ai.astplayground

import com.github.ai.astplayground.assertionDsl.AstBuilderDsl.buildAst
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