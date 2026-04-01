package com.github.ai.astplayground.parser

import com.github.ai.astplayground.astDsl.AstBuilderDsl.javaAst
import com.github.ai.astplayground.astDsl.ParametersFactory.int
import com.github.ai.astplayground.parseAndAssert
import org.junit.jupiter.api.Test

class ConstructorDeclarationTest {

    @Test
    fun `should support default constructor declaration`() {
        parseAndAssert(
            input = """
                class Test {
                    Test() {
                    }
                }
            """,
            expected = javaAst {
                `class`("Test") {
                    constructor()
                }
            }
        )
    }

    @Test
    fun `should support multiple constructor declaration`() {
        parseAndAssert(
            input = """
                class Test {
                    Test() {
                    }
                    Test(int i0, int i1) {
                    }
                }
            """,
            expected = javaAst {
                `class`("Test") {
                    constructor()
                    constructor(
                        int("i0"),
                        int("i1")
                    )
                }
            }
        )
    }
}