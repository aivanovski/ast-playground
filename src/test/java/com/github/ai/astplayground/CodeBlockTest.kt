package com.github.ai.astplayground

import com.github.ai.astplayground.assertionDsl.AstBuilderDsl.buildAst
import com.github.ai.astplayground.assertionDsl.ExprFactory.invoke
import com.github.ai.astplayground.assertionDsl.ExpressionFactory
import com.github.ai.astplayground.assertionDsl.ExpressionFactory.string
import com.github.ai.astplayground.assertionDsl.IdentifierFactory.field
import com.github.ai.astplayground.assertionDsl.IdentifierFactory.method
import com.github.ai.astplayground.assertionDsl.InitializerFactory
import com.github.ai.astplayground.assertionDsl.InitializerFactory.stringValue
import com.github.ai.astplayground.assertionDsl.ParametersFactory
import com.github.ai.astplayground.assertionDsl.TypeReferenceFactory
import com.github.ai.astplayground.assertionDsl.TypeReferenceFactory.void
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
                        variable("s0", TypeReferenceFactory.string(), stringValue("abc"))
                        variable("s1", TypeReferenceFactory.string(), stringValue("123"))
                        `return`(
                            Expression.BinaryExpression(
                                Operator.PLUS,
                                Expression.Identifier("s0"),
                                Expression.Identifier("s1")
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
                                Expression.Identifier("a"),
                                Expression.Identifier("b")
                            )
                        )
                    }

                    method("m0", returns = void()) {
                        variable(
                            "i",
                            TypeReferenceFactory.int(),
                            InitializerBlock.ExpressionBlock(
                                Expression.MethodInvocation(
                                    arguments = listOf(
                                        Expression.IntLiteral(1),
                                        Expression.IntLiteral(2)
                                    ),
                                    method = Expression.Identifier("sum")
                                )
                            )
                        )
                    }
                }
            }
        )
    }
}