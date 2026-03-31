package com.github.ai.astplayground.parser

import com.github.ai.astplayground.astDsl.AstBuilderDsl.buildAst
import com.github.ai.astplayground.astDsl.ExpressionFactory.invoke
import com.github.ai.astplayground.astDsl.ExpressionFactory.literal
import com.github.ai.astplayground.astDsl.ExpressionFactory.string
import com.github.ai.astplayground.astDsl.IdentifierFactory.asIdentifier
import com.github.ai.astplayground.astDsl.IdentifierFactory.field
import com.github.ai.astplayground.astDsl.IdentifierFactory.method
import com.github.ai.astplayground.astDsl.InitializerFactory.iliteral
import com.github.ai.astplayground.astDsl.ParametersFactory
import com.github.ai.astplayground.astDsl.TypeReferenceFactory
import com.github.ai.astplayground.astDsl.TypeReferenceFactory.asType
import com.github.ai.astplayground.astDsl.TypeReferenceFactory.void
import com.github.ai.astplayground.parseAndAssert
import com.github.ai.astplayground.transpiler.model.Expression
import com.github.ai.astplayground.transpiler.model.InitializerBlock
import com.github.ai.astplayground.transpiler.model.Operator
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
                        call("System" field "out" method "println" invoke string("Hello"))
                    }
                }
            }
        )
    }

    @Test
    fun `should support simple expressions`() {
        parseAndAssert(
            input = """
                class Test {
                    String m0() {
                        return "abc" + "123";
                    }
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    method("m0", returns = TypeReferenceFactory.string()) {
                        `return`(string("abc123"))
                    }
                }
            }
        )
    }

    @Test
    fun `should support variable declaration`() {
        parseAndAssert(
            input = """
                class Test {
                    String m0() {
                        String s0 = "abc";
                        String s1 = "123";
                        return s0 + s1;
                    }
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    method("m0", returns = TypeReferenceFactory.string()) {
                        variable("s0", "String".asType(), "abc".iliteral())
                        variable("s1", "String".asType(), "123".iliteral())
                        `return`(
                            Expression.BinaryExpression(
                                Operator.PLUS,
                                "s0".asIdentifier(),
                                "s1".asIdentifier()
                            )
                        )
                    }
                }
            }
        )
    }

    @Test
    fun `should support method invocation`() {
        parseAndAssert(
            input = """
                class Test {
                    int sum(int a, int b) {
                        return a + b;
                    }
                    void m0() {
                        int i = sum(1, 2);
                    }
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    method(
                        "sum",
                        ParametersFactory.int("a"),
                        ParametersFactory.int("b"),
                        returns = TypeReferenceFactory.int()
                    ) {
                        `return`(
                            Expression.BinaryExpression(
                                Operator.PLUS,
                                "a".asIdentifier(),
                                "b".asIdentifier()
                            )
                        )
                    }

                    method("m0", returns = void()) {
                        variable(
                            "i",
                            TypeReferenceFactory.int(),
                            InitializerBlock.ExpressionBlock(
                                Expression.MethodInvocation(
                                    arguments = listOf(1.literal(), 2.literal()),
                                    method = "sum".asIdentifier()
                                )
                            )
                        )
                    }
                }
            }
        )
    }
}