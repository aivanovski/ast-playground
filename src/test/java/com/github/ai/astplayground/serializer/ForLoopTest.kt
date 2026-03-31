package com.github.ai.astplayground.serializer

import com.github.ai.astplayground.astDsl.AstBuilderDsl.buildAst
import com.github.ai.astplayground.astDsl.ExpressionFactory.int
import com.github.ai.astplayground.astDsl.ExpressionFactory.invoke
import com.github.ai.astplayground.astDsl.ExpressionFactory.string
import com.github.ai.astplayground.astDsl.IdentifierFactory.asIdentifier
import com.github.ai.astplayground.astDsl.IdentifierFactory.field
import com.github.ai.astplayground.astDsl.IdentifierFactory.method
import com.github.ai.astplayground.astDsl.InitializerFactory.intValue
import com.github.ai.astplayground.astDsl.ParametersFactory
import com.github.ai.astplayground.astDsl.TypeReferenceFactory
import com.github.ai.astplayground.astDsl.TypeReferenceFactory.parameterizedWith
import com.github.ai.astplayground.astDsl.TypeReferenceFactory.void
import com.github.ai.astplayground.astDsl.VariableFactory.asIntVariable
import com.github.ai.astplayground.serializeAndAssert
import com.github.ai.astplayground.transpiler.model.Expression
import com.github.ai.astplayground.transpiler.model.Operator
import org.junit.jupiter.api.Test

class ForLoopTest {

    @Test
    fun `should support for loop`() {
        serializeAndAssert(
            input = buildAst {
                `class`("Test") {
                    method("m0", returns = void()) {
                        call(
                            Expression.ForLoop(
                                initializers = listOf(
                                    Expression.DeclareVariable(
                                        name = "i",
                                        type = TypeReferenceFactory.int(),
                                        initializer = intValue(0)
                                    )
                                ),
                                condition = Expression.BinaryExpression(
                                    operator = Operator.LESS_THAN,
                                    lhs = Expression.Identifier("i"),
                                    rhs = int(10)
                                ),
                                updates = listOf(
                                    Expression.Assignment(
                                        variable = Expression.Identifier("i"),
                                        expression = Expression.BinaryExpression(
                                            operator = Operator.PLUS,
                                            lhs = Expression.Identifier("i"),
                                            rhs = int(1)
                                        )
                                    )
                                ),
                                body = "System" field "out" method "println" invoke string("Hello")
                            )
                        )
                    }
                }
            },
            // TODO: refactor, is it possible to convert it into for loop?
            expected = """
                class Test {
                    fun m0() {
                        var i: Int = 0
                        while (i < 10) {
                            System.out.println("Hello")
                            i = i + 1
                        }
                    }
                }
            """.trimIndent()
        )
    }

    @Test
    fun `should support for each loop`() {
        serializeAndAssert(
            input = buildAst {
                import("java.util.List")

                `class`("Test") {
                    method(
                        "m0",
                        ParametersFactory.variable(
                            "nums",
                            "List" parameterizedWith "Integer"
                        ),
                        returns = void()
                    ) {
                        foreach(
                            "i".asIntVariable(),
                            "nums".asIdentifier()
                        ) {
                            call("System" field "out" method "println" invoke "i".asIdentifier())
                        }
                    }
                }
            },
            expected = """
                import java.util.List
                class Test {
                    fun m0(nums: List<Integer>?) {
                        for (i in (nums ?: emptyList())) {
                            System.out.println(i)
                        }
                    }
                }
            """.trimIndent()
        )
    }
}
