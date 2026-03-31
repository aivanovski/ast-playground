package com.github.ai.astplayground.parser

import com.github.ai.astplayground.astDsl.AstBuilderDsl.buildAst
import com.github.ai.astplayground.astDsl.ExpressionFactory.and
import com.github.ai.astplayground.astDsl.ExpressionFactory.equal
import com.github.ai.astplayground.astDsl.ExpressionFactory.invoke
import com.github.ai.astplayground.astDsl.ExpressionFactory.literal
import com.github.ai.astplayground.astDsl.ExpressionFactory.or
import com.github.ai.astplayground.astDsl.ExpressionFactory.string
import com.github.ai.astplayground.astDsl.IdentifierFactory.asIdentifier
import com.github.ai.astplayground.astDsl.IdentifierFactory.field
import com.github.ai.astplayground.astDsl.IdentifierFactory.method
import com.github.ai.astplayground.astDsl.ParametersFactory
import com.github.ai.astplayground.parseAndAssert
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
