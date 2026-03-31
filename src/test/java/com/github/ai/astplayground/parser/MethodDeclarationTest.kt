package com.github.ai.astplayground.parser

import com.github.ai.astplayground.astDsl.AstBuilderDsl.buildAst
import com.github.ai.astplayground.astDsl.Modifiers.static
import com.github.ai.astplayground.astDsl.ParametersFactory.int
import com.github.ai.astplayground.astDsl.TypeReferenceFactory
import com.github.ai.astplayground.astDsl.TypeReferenceFactory.asType
import com.github.ai.astplayground.parseAndAssert
import org.junit.jupiter.api.Test

class MethodDeclarationTest {

    @Test
    fun `should support method declaration`() {
        parseAndAssert(
            input = """
                class Test {
                    void m0() {
                    }
                    Object m1() {
                    }
                    void m2(int i0, int i1) {
                    }
                    int m3() {
                    }
                    static void m4() {
                    }
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    void_method("m0")
                    method("m1", returns = "Object".asType())
                    void_method("m2", int("i0"), int("i1"))
                    method("m3", returns = TypeReferenceFactory.int())
                    void_method("m4", modifiers = static())
                }
            }
        )
    }
}