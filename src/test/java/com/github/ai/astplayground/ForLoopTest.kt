package com.github.ai.astplayground

import com.github.ai.astplayground.assertionDsl.AstBuilderDsl.buildAst
import com.github.ai.astplayground.assertionDsl.ExpressionFactory.int
import com.github.ai.astplayground.assertionDsl.ExpressionFactory.invoke
import com.github.ai.astplayground.assertionDsl.ExpressionFactory.string
import com.github.ai.astplayground.assertionDsl.IdentifierFactory.field
import com.github.ai.astplayground.assertionDsl.IdentifierFactory.asIdentifier
import com.github.ai.astplayground.assertionDsl.IdentifierFactory.method
import com.github.ai.astplayground.assertionDsl.InitializerFactory.intValue
import com.github.ai.astplayground.assertionDsl.ParametersFactory
import com.github.ai.astplayground.assertionDsl.TypeReferenceFactory
import com.github.ai.astplayground.assertionDsl.TypeReferenceFactory.parameterizedWith
import com.github.ai.astplayground.assertionDsl.TypeReferenceFactory.void
import com.github.ai.astplayground.assertionDsl.VariableFactory.asIntVariable
import com.github.ai.astplayground.transpiler.model.Expression
import com.github.ai.astplayground.transpiler.model.Operator
import org.junit.jupiter.api.Test

class ForLoopTest {

    @Test
    fun `should support for loop`() {
        parseAndAssert(
            input = """
                class Test {
                    void m0() {
                        for (int i = 0; i < 10; i = i + 1) {
                            System.out.println("Hello");
                        }
                    }
                }
            """,
            expected = buildAst {
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
            }
        )
    }

    @Test
    fun `should support for each loop`() {
        parseAndAssert(
            input = """
                import java.util.List;

                class Test {
                    void m0(List<Integer> nums) {
                        for (int i : nums) {
                            System.out.println(i);
                        }
                    }
                }
            """,
            expected = buildAst {
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
            }
        )
    }
}
