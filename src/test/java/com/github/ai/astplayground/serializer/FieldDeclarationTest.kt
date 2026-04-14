package com.github.ai.astplayground.serializer

import com.github.ai.astplayground.transpileJavaAndAssert
import org.junit.jupiter.api.Test

class FieldDeclarationTest {

    @Test
    fun `should support primitive declarations`() {
        transpileJavaAndAssert(
            input = """
                class Test {
                    boolean bl;
                    boolean bl0 = true;
                    boolean bl1 = false;

                    byte b;
                    byte b0 = 0x01;

                    char c;
                    char c0 = 'a';

                    int i;
                    int i0 = 1;

                    long l;
                    long l0 = 1;

                    float f;
                    float f0 = 1f;
                    float f1 = 2.0f;
                    float f2 = 0.3f;

                    double d;
                    double d0 = 1.0d;
                    double d1 = 0.2d;
                }
            """,
            expected = """
                class Test {
                    var bl: Boolean = false
                    var bl0: Boolean = true
                    var bl1: Boolean = false

                    var b: Byte = 0
                    var b0: Byte = 1

                    var c: Char = 0.toChar()
                    var c0: Char = 'a'

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
        transpileJavaAndAssert(
            input = """
                class Test {
                    Object o;
                    String s;
                    String s0 = "abc";
                    StringBuilder sb = new StringBuilder("cde");
                }
            """,
            expected = """
                class Test {
                    var o: Object? = null
                    var s: String? = null
                    var s0: String = "abc"
                    var sb: StringBuilder = StringBuilder("cde")
                }
            """
        )
    }
}