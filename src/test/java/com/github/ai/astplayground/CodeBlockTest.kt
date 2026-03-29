package com.github.ai.astplayground

import com.github.ai.astplayground.assertionDsl.AstBuilderDsl.buildAst
import com.github.ai.astplayground.assertionDsl.ExpressionFactory.string
import com.github.ai.astplayground.assertionDsl.TypeReferenceFactory.void
import org.junit.jupiter.api.Test

class CodeBlockTest {

    @Test
    fun `should support method body`() {
        parseAndAssert(
            input = """
                class Test {
                    void m0() {
                        System.out.println("Hello");
                    }
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    method("m0", returns = void()) {
                        identifier("System")
                            .fieldAccess("out")
                            .invoke("println", string("Hello"))
                    }
                }
            }
        )
    }
}