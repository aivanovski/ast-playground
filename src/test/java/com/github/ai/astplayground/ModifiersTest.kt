package com.github.ai.astplayground

import org.junit.jupiter.api.Test

class ModifiersTest {

    @Test
    fun `should convert class visibility modifiers`() {
        val input = """
            class Test {
                public class Test0 {
                }
                private class Test1 {
                }
            }
        """

        val output = """
            class Test {
                class Test0 {
                }
                private class Test1 {
                }
            }
        """

        transpileAndAssert(input, output)
    }

    @Test
    fun `should convert variable visibility modifiers`() {
        val input = """
            class Test {
                public int a = 1;
                private int b = 2;
                protected int c = 3;
                int d = 4;
            }
        """

        val output = """
            class Test {
                var a: Int = 1
                private var b: Int = 2
                protected var c: Int = 3
                var d: Int = 4
            }
        """

        transpileAndAssert(input, output)
    }

    @Test
    fun `should convert method visibility modifiers`() {
        val input = """
            class Test {
                public void m0() {
                }
                private void m1() {
                }
                protected void m2() {
                }
                void m3() {
                }
            }
        """

        val output = """
            class Test {
                fun m0() {
                }
                private fun m1() {
                }
                protected fun m2() {
                }
                fun m3() {
                }
            }
        """

        transpileAndAssert(input, output)
    }
}