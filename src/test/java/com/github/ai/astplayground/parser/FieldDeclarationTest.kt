package com.github.ai.astplayground.parser

import com.github.ai.astplayground.astDsl.AstBuilderDsl.buildAst
import com.github.ai.astplayground.astDsl.ExpressionFactory.literal
import com.github.ai.astplayground.astDsl.ExpressionFactory.typedIdentifier
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
import com.github.ai.astplayground.astDsl.InitializerFactory
import com.github.ai.astplayground.astDsl.InitializerFactory.initializer
import com.github.ai.astplayground.astDsl.TypeReferenceFactory.asType
import com.github.ai.astplayground.astDsl.TypeReferenceFactory.parameterizedType
import com.github.ai.astplayground.parseAndAssert
import org.junit.jupiter.api.Test

class FieldDeclarationTest {

    @Test
    fun `should support boolean declarations`() {
        parseAndAssert(
            input = """
                class Test {
                    boolean b;
                    boolean b0 = true;
                    boolean b1 = false;
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    field(boolean("b"))
                    field(boolean("b0", true))
                    field(boolean("b1", false))
                }
            }
        )
    }

    @Test
    fun `should support byte declarations`() {
        parseAndAssert(
            input = """
                class Test {
                    byte b;
                    byte b0 = 1;
                    byte b1 = 0x02;
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    field(byte("b"))
                    field(byte("b0", 1))
                    field(byte("b1", 2))
                }
            }
        )
    }

    @Test
    fun `should support char declarations`() {
        parseAndAssert(
            input = """
                class Test {
                    char c;
                    char c0 = 1;
                    char c1 = 'a';
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    field(char("c"))
                    field(char("c0", 1.toChar()))
                    field(char("c1", 'a'))
                }
            }
        )
    }

    @Test
    fun `should support int declarations`() {
        parseAndAssert(
            input = """
                class Test {
                    int i;
                    int i0 = 1;
                    int i1 = 0x02;
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    field(int("i"))
                    field(int("i0", 1))
                    field(int("i1", 2))
                }
            }
        )
    }

    @Test
    fun `should support long declarations`() {
        parseAndAssert(
            input = """
                class Test {
                    long l;
                    long l0 = 1L;
                    long l1 = 2l;
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    field(long("l"))
                    field(long("l0", 1L))
                    field(long("l1", 2L))
                }
            }
        )
    }

    @Test
    fun `should support float declarations`() {
        parseAndAssert(
            input = """
                class Test {
                    float f;
                    float f0 = 1;
                    float f1 = 2f;
                    float f2 = 3F;
                    float f3 = 0.4F;
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    field(float("f"))
                    field(float("f0", 1.0f))
                    field(float("f1", 2.0f))
                    field(float("f2", 3.0f))
                    field(float("f3", 0.4f))
                }
            }
        )
    }

    @Test
    fun `should support double declarations`() {
        parseAndAssert(
            input = """
                class Test {
                    double d;
                    double d0 = 1;
                    double d1 = 2d;
                    double d2 = 3D;
                    double d3 = 0.4;
                    double d4 = 0.5d;
                    double d5 = 0.6D;
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    field(double("d"))
                    field(double("d0", 1.0))
                    field(double("d1", 2.0))
                    field(double("d2", 3.0))
                    field(double("d3", 0.4))
                    field(double("d4", 0.5))
                    field(double("d5", 0.6))
                }
            }
        )
    }

    @Test
    fun `should support typed declarations`() {
        parseAndAssert(
            input = """
                class Test {
                    Object o;
                    Object o0 = null;
                    String s;
                    String s0 = null;
                    String s1 = "abc";
                    StringBuilder sb = new StringBuilder("cde");
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    field(variable("o", "Object".asType()))
                    field(variable("o0", "Object".asType()))
                    field(variable("s", "String".asType()))
                    field(variable("s0", "String".asType()))
                    field(variable("s1", "String".asType(), initializer { "abc".literal() }))
                    field(
                        variable(
                            "sb",
                            "StringBuilder".asType(),
                            initializer {
                                "StringBuilder" invokeConstructor listOf("cde".literal())
                            }
                        )
                    )
                }
            }
        )
    }

    @Test
    fun `should support parameterized types`() {
        parseAndAssert(
            input = """
                class Test {
                    List<String> values = new ArrayList<String>();
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    field(
                        variable(
                            "values",
                            parameterizedType("List", parameterizedWith = "String"),
                            initializer = InitializerFactory.constructor(
                                typedIdentifier("ArrayList", "String")
                            )
                        )
                    )
                }
            }
        )
    }
}
