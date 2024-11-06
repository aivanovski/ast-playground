package com.github.ai.astplayground

import org.junit.jupiter.api.Test

class CodeBlockTest {

    @Test
    fun `should convert method with local variables`() {
        val input = """
            class Test {
                String m0() {
                    String s0 = "abc";
                    String s1 = "def";
                    return s0 + s1;
                }
            }
        """

        val output = """
            class Test {
                fun m0(): String? {
                    var s0: String? = "abc"
                    var s1: String? = "def"
                    return s0 + s1
                }
            }
        """

        transpileAndAssert(input, output)
    }

    @Test
    fun `should convert member method invocation`() {
        val input = """
            class Test {
                int sum(int a, int b) {
                    return a + b;
                }
                void m1() {
                    int sum = sum(1, 1);
                    System.out.println("sum=" + sum);
                }
            }
        """

        val output = """
            class Test {
                fun sum(a: Int, b: Int): Int {
                    return a + b
                }
                fun m1() {
                    var sum: Int = sum(1, 1)
                    System.out.println("sum=" + sum)
                }
            }
        """

        transpileAndAssert(input, output)
    }

    @Test
    fun `should convert static method invocation`() {
        val input = """
            class Test {
                void m0() {
                    m1();
                }
                static void m1() {
                }
            }
        """

        val output = """
            class Test {
                fun m0() {
                    m1()
                }
                companion object {
                    fun m1() {
                    }
                }
            }
        """

        transpileAndAssert(input, output)
    }
}