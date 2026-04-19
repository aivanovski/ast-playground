package com.github.ai.astplayground.parser

import com.github.ai.astplayground.astDsl.AstBuilderDsl.javaAst
import com.github.ai.astplayground.astDsl.ExpressionFactory.invoke
import com.github.ai.astplayground.astDsl.ExpressionFactory.literal
import com.github.ai.astplayground.astDsl.ExpressionFactory.string
import com.github.ai.astplayground.astDsl.IdentifierFactory.asIdentifier
import com.github.ai.astplayground.astDsl.IdentifierFactory.field
import com.github.ai.astplayground.astDsl.IdentifierFactory.method
import com.github.ai.astplayground.astDsl.IdentifierFactory.plus
import com.github.ai.astplayground.astDsl.InitializerFactory.iliteral
import com.github.ai.astplayground.astDsl.ParametersFactory.int
import com.github.ai.astplayground.astDsl.TypeReferenceFactory
import com.github.ai.astplayground.astDsl.TypeReferenceFactory.asType
import com.github.ai.astplayground.astDsl.TypeReferenceFactory.void
import com.github.ai.astplayground.parseAndAssert
import com.github.ai.astplayground.transpiler.parser.model.JExpression
import com.github.ai.astplayground.transpiler.parser.model.JInitializerBlock
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
                    String m1() {
                        return "abc" + "123";
                    }
                    String m2() {
                        String s0 = "abc";
                        String s1 = "123";
                        return s0 + s1;
                    }
                    int sum(int a, int b) {
                        return a + b;
                    }
                    void m3() {
                        int i = sum(1, 2);
                    }
                }
            """,
            expected = javaAst {
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
                            JInitializerBlock.ExpressionBlock(
                                JExpression.MethodInvocation(
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