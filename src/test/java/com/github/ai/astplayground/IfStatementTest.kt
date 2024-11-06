package com.github.ai.astplayground

import org.junit.jupiter.api.Test

class IfStatementTest {

    @Test
    fun `should convert if with code block inside`() {
        val input = """
            class Test {
                void m0() {
                    if (true) {
                        System.out.println("abc");
                    }
                }
            }
        """

        val output = """
            class Test {
                fun m0() {
                    if (true) {
                        System.out.println("abc")
                    }
                }
            }
        """

        transpileAndAssert(input, output)
    }

    @Test
    fun `should convert if with return statement`() {
        val input = """
            class Test {
                void m0() {
                    if (true) return;
                }
            }
        """

        val output = """
            class Test {
                fun m0() {
                    if (true) return
                }
            }
        """

        transpileAndAssert(input, output)
    }
}