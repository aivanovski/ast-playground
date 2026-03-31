package com.github.ai.astplayground.serializer

import com.github.ai.astplayground.astDsl.AstBuilderDsl.buildAst
import com.github.ai.astplayground.astDsl.ExpressionFactory.literal
import com.github.ai.astplayground.astDsl.Modifiers.static
import com.github.ai.astplayground.astDsl.ParametersFactory.int
import com.github.ai.astplayground.astDsl.TypeReferenceFactory
import com.github.ai.astplayground.astDsl.TypeReferenceFactory.asType
import com.github.ai.astplayground.serializeAndAssert
import com.github.ai.astplayground.transpiler.model.Expression.Null
import org.junit.jupiter.api.Test

class MethodDeclarationTest {

    @Test
    fun `should support method declaration`() {
        serializeAndAssert(
            input = buildAst {
                `class`("Test") {
                    void_method("m0")
                    method("m1", returns = "Object".asType()) {
                        `return`(Null)
                    }
                    void_method("m2", int("i0"), int("i1"))
                    method("m3", returns = TypeReferenceFactory.int()) {
                        `return`(1.literal())
                    }
                    void_method("sm0", modifiers = static())
                }
            },
            expected = """
                class Test {
                    fun m0() {}
                    fun m1(): Object? {
                        return null
                    }
                    fun m2(i0: Int, i1: Int) {}
                    fun m3(): Int {
                        return 1
                    }
                    companion object {
                        fun sm0() {}
                    }
                }
            """.trimIndent()
        )
    }
}