package com.github.ai.astplayground.serializer

import com.github.ai.astplayground.astDsl.AstBuilderDsl.javaAst
import com.github.ai.astplayground.astDsl.ExpressionFactory.literal
import com.github.ai.astplayground.astDsl.FieldFactory.boolean
import com.github.ai.astplayground.astDsl.FieldFactory.byte
import com.github.ai.astplayground.astDsl.FieldFactory.char
import com.github.ai.astplayground.astDsl.FieldFactory.double
import com.github.ai.astplayground.astDsl.FieldFactory.float
import com.github.ai.astplayground.astDsl.FieldFactory.int
import com.github.ai.astplayground.astDsl.FieldFactory.long
import com.github.ai.astplayground.astDsl.FieldFactory.string
import com.github.ai.astplayground.astDsl.FieldFactory.variable
import com.github.ai.astplayground.astDsl.IdentifierFactory.invokeConstructor
import com.github.ai.astplayground.astDsl.InitializerFactory.initializer
import com.github.ai.astplayground.astDsl.TypeReferenceFactory.asType
import com.github.ai.astplayground.transpileAndAssert
import org.junit.jupiter.api.Test

class FieldDeclarationTest {

    @Test
    fun `should support primitive declarations`() {
        transpileAndAssert(
            input = javaAst {
                `class`("Test") {
                    field(boolean("bl"))
                    field(boolean("bl0", true))
                    field(boolean("bl1", false))

                    field(byte("b"))
                    field(byte("b0", 1))

                    field(char("c"))
                    field(char("c0", 'a'))

                    field(int("i"))
                    field(int("i0", 1))

                    field(long("l"))
                    field(long("l0", 1))

                    field(float("f"))
                    field(float("f0", 1f))
                    field(float("f1", 2.0f))
                    field(float("f2", 0.3f))

                    field(double("d"))
                    field(double("d0", 1.0))
                    field(double("d1", 0.2))
                }
            },
            expected = """
                class Test {
                    var bl: Boolean = false
                    var bl0: Boolean = true
                    var bl1: Boolean = false

                    var b: Byte = 0
                    var b0: Byte = 1

                    var c: Char = 0.toChar()
                    var c0: Char = ${'a'.code}.toChar()

                    var i: Int = 0
                    var i0: Int = 1

                    var l: Long = 0L
                    var l0: Long = 1L

                    var f: Float = 0F
                    var f0: Float = 1.0F
                    var f1: Float = 2.0F
                    var f2: Float = 0.3F

                    var d: Double = 0.0
                    var d0: Double = 1.0
                    var d1: Double = 0.2
                }
            """
        )
    }

    @Test
    fun `should support typed declarations`() {
        transpileAndAssert(
            input = javaAst {
                `class`("Test") {
                    field(variable("o", "Object".asType()))
                    field(variable("s", "String".asType()))
                    field(string("s0", "abc"))
                    field(
                        variable(
                            "sb",
                            "StringBuilder".asType(),
                            initializer { "StringBuilder" invokeConstructor listOf("cde".literal()) }
                        )
                    )
                }
            },
            expected = """
                class Test {
                    var o: Object? = null
                    var s: String? = null
                    var s0: String = "abc"
                    var sb: StringBuilder = StringBuilder("cde")
                }
            """.trimIndent()
        )
    }
}