package com.github.ai.astplayground.parser

import com.github.ai.astplayground.astDsl.AstBuilderDsl.javaAst
import com.github.ai.astplayground.astDsl.ExpressionFactory.int
import com.github.ai.astplayground.astDsl.ExpressionFactory.invoke
import com.github.ai.astplayground.astDsl.ExpressionFactory.string
import com.github.ai.astplayground.astDsl.IdentifierFactory.field
import com.github.ai.astplayground.astDsl.IdentifierFactory.asIdentifier
import com.github.ai.astplayground.astDsl.IdentifierFactory.method
import com.github.ai.astplayground.astDsl.InitializerFactory.intValue
import com.github.ai.astplayground.astDsl.ParametersFactory
import com.github.ai.astplayground.astDsl.TypeReferenceFactory
import com.github.ai.astplayground.astDsl.TypeReferenceFactory.parameterizedWith
import com.github.ai.astplayground.astDsl.TypeReferenceFactory.void
import com.github.ai.astplayground.astDsl.VariableFactory.asIntVariable
import com.github.ai.astplayground.parseAndAssert
import com.github.ai.astplayground.transpiler.parser.model.JExpression
import com.github.ai.astplayground.transpiler.parser.model.Operator
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
            expected = javaAst {
                `class`("Test") {
                    method("m0", returns = void()) {
                        call(
                            JExpression.ForLoop(
                                initializers = listOf(
                                    JExpression.DeclareVariable(
                                        name = "i",
                                        type = TypeReferenceFactory.int(),
                                        initializer = intValue(0)
                                    )
                                ),
                                condition = JExpression.BinaryExpression(
                                    operator = Operator.LESS_THAN,
                                    lhs = JExpression.Identifier("i"),
                                    rhs = int(10)
                                ),
                                updates = listOf(
                                    JExpression.Assignment(
                                        variable = JExpression.Identifier("i"),
                                        expression = JExpression.BinaryExpression(
                                            operator = Operator.PLUS,
                                            lhs = JExpression.Identifier("i"),
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
            expected = javaAst {
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
