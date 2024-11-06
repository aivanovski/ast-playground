package com.github.ai.astplayground

import org.junit.jupiter.api.Test

class StringsTest {

    @Test
    fun `should convert string declaration`() {
        val input = """
            class Test {
                String s0 = "abc";
                String s1;
            }
        """

        val output = """
            class Test {
                var s0: String? = "abc"
                var s1: String? = null
            }
        """

        transpileAndAssert(input, output)
    }

    @Test
    fun `should convert string concatenation`() {
        val input = """
            class Test {
                String s0 = "abc" + "def";
                String s1 = s0 + "ghi";
                String s2 = s0 + s1;
            }
        """

        val output = """
            class Test {
                var s0: String? = "abc" + "def"
                var s1: String? = s0 + "ghi"
                var s2: String? = s0 + s1
            }
        """

        transpileAndAssert(input, output)
    }
}