package com.github.ai.astplayground.serializer

import com.github.ai.astplayground.astDsl.AstBuilderDsl.javaAst
import com.github.ai.astplayground.astDsl.ExpressionFactory.int
import com.github.ai.astplayground.astDsl.ExpressionFactory.invoke
import com.github.ai.astplayground.astDsl.ExpressionFactory.string
import com.github.ai.astplayground.astDsl.IdentifierFactory.field
import com.github.ai.astplayground.astDsl.IdentifierFactory.method
import com.github.ai.astplayground.astDsl.InitializerFactory.intValue
import com.github.ai.astplayground.astDsl.TypeReferenceFactory
import com.github.ai.astplayground.astDsl.TypeReferenceFactory.void
import com.github.ai.astplayground.transpileAndAssert
import com.github.ai.astplayground.transpileJavaAndAssert
import com.github.ai.astplayground.transpiler.parser.model.JExpression
import com.github.ai.astplayground.transpiler.parser.model.Operator
import org.junit.jupiter.api.Test

class ForLoopTest {

    @Test
    fun `should support for loop`() {
        transpileAndAssert(
            input = javaAst {
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
            """
        )
    }

    @Test
    fun `should support for each loop`() {
        transpileJavaAndAssert(
            input = """
                import java.util.List;
                class Test {
                    void m0(List<Integer> nums) {
                        for (Integer i : nums) {
                            System.out.println(i);
                        }
                    }
                }
            """,
            expected = """
                import java.util.List
                class Test {
                    fun m0(nums: List<Integer?>?) {
                        for (i in nums!!) {
                            System.out.println(i)
                        }
                    }
                }
            """
        )
    }
}
