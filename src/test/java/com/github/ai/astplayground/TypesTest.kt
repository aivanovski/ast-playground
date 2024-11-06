package com.github.ai.astplayground

import org.junit.jupiter.api.Test

class TypesTest {

    @Test
    fun `should convert class definition`() {
        val input = """
            class Test {
            }
        """

        val output = """
            class Test {
            }
        """

        transpileAndAssert(input, output)
    }

    @Test
    fun `should convert inner class definition`() {
        val input = """
            class Test {
                class Test0 {
                }
                class Test1 {
                }
            }
        """

        val output = """
            class Test {
                class Test0 {
                }
                class Test1 {
                }
            }
        """

        transpileAndAssert(input, output)
    }
}