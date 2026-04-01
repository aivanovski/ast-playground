package com.github.ai.astplayground.serializer

import com.github.ai.astplayground.astDsl.AstBuilderDsl.javaAst
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
import com.github.ai.astplayground.transpileAndAssert
import org.junit.jupiter.api.Test

class IfStatementTest {

    @Test
    fun `should support if statements`() {
        transpileAndAssert(
            input = javaAst {
                `class`("Test") {
                    void_method("m0", ParametersFactory.int("i")) {
                        `if`(true.literal()) {
                            call("System" field "out" method "println" invoke string("Hello"))
                        }
                        `if`(true.literal() and false.literal()) {
                            call("System" field "out" method "println" invoke string("Hello"))
                        }
                        `if`(true.literal() or false.literal()) {
                            call("System" field "out" method "println" invoke string("Hello"))
                        }
                        `if`("i".asIdentifier() equal 1.literal()) {
                            call("System" field "out" method "println" invoke string("Hello"))
                        }
                    }
                }
            },
            expected = """
                class Test {
                    fun m0(i: Int) {
                        if (true) {
                            System.out.println("Hello")
                        }
                        if (true && false) {
                            System.out.println("Hello")
                        }
                        if (true || false) {
                            System.out.println("Hello")
                        }
                        if (i == 1) {
                            System.out.println("Hello")
                        }
                    }
                }
            """.trimIndent()
        )
    }
}
