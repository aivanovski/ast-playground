package com.github.ai.astplayground.serializer

import com.github.ai.astplayground.astDsl.AstBuilderDsl.buildAst
import com.github.ai.astplayground.astDsl.ExpressionFactory.invoke
import com.github.ai.astplayground.astDsl.ExpressionFactory.literal
import com.github.ai.astplayground.astDsl.ExpressionFactory.string
import com.github.ai.astplayground.astDsl.IdentifierFactory.asIdentifier
import com.github.ai.astplayground.astDsl.IdentifierFactory.field
import com.github.ai.astplayground.astDsl.IdentifierFactory.method
import com.github.ai.astplayground.astDsl.IdentifierFactory.plus
import com.github.ai.astplayground.astDsl.InitializerFactory
import com.github.ai.astplayground.astDsl.InitializerFactory.iliteral
import com.github.ai.astplayground.astDsl.ParametersFactory.int
import com.github.ai.astplayground.astDsl.TypeReferenceFactory
import com.github.ai.astplayground.astDsl.TypeReferenceFactory.asType
import com.github.ai.astplayground.astDsl.TypeReferenceFactory.void
import com.github.ai.astplayground.serializeAndAssert
import com.github.ai.astplayground.transpiler.model.Expression
import org.junit.jupiter.api.Test

class CodeBlockTest {

    @Test
    fun `should support method body`() {
        serializeAndAssert(
            input = buildAst {
                `class`("Test") {
                    method("m0", returns = void()) {
                        call("System" field "out" method "println" invoke string("Hello"))
                    }

                    method("m1", returns = TypeReferenceFactory.string()) {
                        `return`(string("abc123"))
                    }

                    method("m2", returns = TypeReferenceFactory.string()) {
                        variable("s0", "String".asType(), "abc".iliteral())
                        variable("s1", "String".asType(), "123".iliteral())
                        `return`("s0".asIdentifier() plus "s1".asIdentifier())
                    }

                    method("sum", int("a"), int("b"), returns = TypeReferenceFactory.int()) {
                        `return`("a".asIdentifier() plus "b".asIdentifier())
                    }

                    method("m3", returns = void()) {
                        variable(
                            "i",
                            TypeReferenceFactory.int(),
                            InitializerFactory.expression(
                                Expression.MethodInvocation(
                                    arguments = listOf(1.literal(), 2.literal()),
                                    method = "sum".asIdentifier()
                                )
                            )
                        )
                    }
                }
            },
            expected = """
                class Test {
                    fun m0() {
                        System.out.println("Hello")
                    }
                    fun m1(): String? {
                        return "abc123"
                    }
                    fun m2(): String? {
                        var s0: String = "abc"
                        var s1: String = "123"
                        return s0 + s1
                    }
                    fun sum(a: Int, b: Int): Int {
                        return a + b
                    }
                    fun m3() {
                        var i: Int = sum(1, 2)
                    }
                }
            """.trimIndent()
        )
    }
}
