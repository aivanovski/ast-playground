package com.github.ai.astplayground

import com.github.ai.astplayground.assertionDsl.AstBuilderDsl.buildAst
import com.github.ai.astplayground.assertionDsl.ExprFactory.invoke
import com.github.ai.astplayground.assertionDsl.ExpressionFactory.int
import com.github.ai.astplayground.assertionDsl.ExpressionFactory.string
import com.github.ai.astplayground.assertionDsl.IdentifierFactory.field
import com.github.ai.astplayground.assertionDsl.IdentifierFactory.method
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
                        "System" field "out" method "println"

                        call("System" field "out" method "println" invoke string("Hello"))
                        call("Math" method "abs" invoke int(1))
                    }
                }
            }
        )
    }
}