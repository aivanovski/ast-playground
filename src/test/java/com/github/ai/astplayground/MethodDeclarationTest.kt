package com.github.ai.astplayground

import com.github.ai.astplayground.assertionDsl.AstBuilderDsl.buildAst
import com.github.ai.astplayground.assertionDsl.Modifiers.static
import com.github.ai.astplayground.assertionDsl.ParametersFactory.int
import com.github.ai.astplayground.assertionDsl.TypeReferenceFactory.type
import com.github.ai.astplayground.assertionDsl.TypeReferenceFactory.void
import org.junit.jupiter.api.Test

class MethodDeclarationTest {

    @Test
    fun `should support method declaration`() {
        parseAndAssert(
            input = """
                class Test {
                    void m0() {
                    }
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    method("m0", returns = void())
                }
            }
        )
    }

    @Test
    fun `should support static method declaration`() {
        parseAndAssert(
            input = """
                class Test {
                    static void m0() {
                    }
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    method("m0", returns = void(), modifiers = static())
                }
            }
        )
    }

    @Test
    fun `should support method returning object`() {
        parseAndAssert(
            input = """
                class Test {
                    Object m0() {
                    }
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    method("m0", returns = type("Object"))
                }
            }
        )
    }

    @Test
    fun `should support method with parameters`() {
        parseAndAssert(
            input = """
                class Test {
                    void m0(int i0, int i1) {
                    }
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    method("m0", int("i0"), int("i1"), returns = void())
                }
            }
        )
    }
}