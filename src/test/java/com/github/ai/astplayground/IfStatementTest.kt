package com.github.ai.astplayground

import com.github.ai.astplayground.assertionDsl.AstBuilderDsl.buildAst
import com.github.ai.astplayground.assertionDsl.ExprFactory.invoke
import com.github.ai.astplayground.assertionDsl.ExpressionFactory.string
import com.github.ai.astplayground.assertionDsl.IdentifierFactory.field
import com.github.ai.astplayground.assertionDsl.IdentifierFactory.method
import com.github.ai.astplayground.assertionDsl.TypeReferenceFactory.void
import com.github.ai.astplayground.transpiler.model.Expression
import org.junit.jupiter.api.Test

class IfStatementTest {

    @Test
    fun `should support if code block`() {
        parseAndAssert(
            input = """
                class Test {
                    void m0() {
                        if (true) {
                            System.out.println("Hello");
                        }
                    }
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    method("m0", returns = void()) {
                        `if`(Expression.BooleanLiteral(true)) {
                            call("System" field "out" method "println" invoke string("Hello"))
                        }
                    }
                }
            }
        )
    }
}