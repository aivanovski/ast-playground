package com.github.ai.astplayground

import com.github.ai.astplayground.assertionDsl.AstBuilderDsl.buildAst
import com.github.ai.astplayground.assertionDsl.ExpressionFactory.and
import com.github.ai.astplayground.assertionDsl.ExpressionFactory.equal
import com.github.ai.astplayground.assertionDsl.ExpressionFactory.int
import com.github.ai.astplayground.assertionDsl.ExpressionFactory.invoke
import com.github.ai.astplayground.assertionDsl.ExpressionFactory.literal
import com.github.ai.astplayground.assertionDsl.ExpressionFactory.or
import com.github.ai.astplayground.assertionDsl.ExpressionFactory.string
import com.github.ai.astplayground.assertionDsl.IdentifierFactory.asIdentifier
import com.github.ai.astplayground.assertionDsl.IdentifierFactory.field
import com.github.ai.astplayground.assertionDsl.IdentifierFactory.method
import com.github.ai.astplayground.assertionDsl.ParametersFactory
import com.github.ai.astplayground.transpiler.model.Expression
import com.github.ai.astplayground.transpiler.model.Operator
import org.junit.jupiter.api.Test

class IfStatementTest {

    @Test
    fun `should support if with literal`() {
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
                    void_method("m0") {
                        `if`(true.literal()) {
                            call("System" field "out" method "println" invoke string("Hello"))
                        }
                    }
                }
            }
        )
    }

    @Test
    fun `should support if with and condition`() {
        parseAndAssert(
            input = """
                class Test {
                    void m0() {
                        if (true && false) {
                            System.out.println("Hello");
                        }
                    }
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    void_method("m0") {
                        `if`(true.literal() and false.literal()) {
                            call("System" field "out" method "println" invoke string("Hello"))
                        }
                    }
                }
            }
        )
    }

    @Test
    fun `should support if with or condition`() {
        parseAndAssert(
            input = """
                class Test {
                    void m0() {
                        if (true || false) {
                            System.out.println("Hello");
                        }
                    }
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    void_method("m0") {
                        `if`(true.literal() or false.literal()) {
                            call("System" field "out" method "println" invoke string("Hello"))
                        }
                    }
                }
            }
        )
    }

    @Test
    fun `should support if with equals condition`() {
        parseAndAssert(
            input = """
                class Test {
                    void m0(int i) {
                        if (i == 1) {
                            System.out.println("Hello");
                        }
                    }
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    void_method("m0", ParametersFactory.int("i")) {
                        `if`("i".asIdentifier() equal 1.literal()) {
                            call("System" field "out" method "println" invoke string("Hello"))
                        }
                    }
                }
            }
        )
    }
}
