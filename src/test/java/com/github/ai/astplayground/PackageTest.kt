package com.github.ai.astplayground

import org.junit.jupiter.api.Test

class PackageTest {

    @Test
    fun `should convert package declaration`() {
        val input = """
            package com.example.test;
            class Test {
            }
        """

        val output = """
            package com.example.test
            class Test {
            }
        """

        transpileAndAssert(input, output)
    }
}