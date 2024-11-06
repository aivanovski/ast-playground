package com.github.ai.astplayground

import org.junit.jupiter.api.Test

class ImportTest {

    @Test
    fun `should convert import declaration`() {
        val input = """
            import java.util.Date;
            class Test {
            }
        """

        val output = """
            import java.util.Date
            class Test {
            }
        """

        transpileAndAssert(input, output)
    }
}