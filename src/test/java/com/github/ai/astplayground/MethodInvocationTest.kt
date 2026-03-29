package com.github.ai.astplayground

import com.github.ai.astplayground.assertionDsl.AstBuilderDsl.buildAst
import com.github.ai.astplayground.assertionDsl.ExpressionFactory.int
import com.github.ai.astplayground.assertionDsl.ExpressionFactory.string
import com.github.ai.astplayground.assertionDsl.TypeReferenceFactory.void
import org.junit.jupiter.api.Test

class MethodInvocationTest {

    @Test
    fun `should support static method call`() {
        parseAndAssert(
            input = """
                class Test {
                    void m0() {
                        System.out.println("Hello");
                        Math.abs(1);
                    }
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    method("m0", returns = void()) {
                        identifier("System")
                            .fieldAccess("out")
                            .invoke("println", string("Hello"))

                        identifier("Math")
                            .invoke("abs", int(1))
                    }
                }
            }
        )
    }
}