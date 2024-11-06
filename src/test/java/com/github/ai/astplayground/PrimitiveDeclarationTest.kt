package com.github.ai.astplayground

import org.junit.jupiter.api.Test

class PrimitiveDeclarationTest {

    @Test
    fun `should convert boolean declaration`() {
        val input = """
            class Test {
                boolean b0 = true;
                boolean b1;
            }
        """

        val output = """
            class Test {
                var b0: Boolean = true
                var b1: Boolean = false
            }
        """

        transpileAndAssert(input, output)
    }

    @Test
    fun `should convert byte declaration`() {
        val input = """
            class Test {
                byte b0 = 1;
                byte b1 = 0xFF;
                byte b2;
            }
        """

        val output = """
            class Test {
                var b0: Byte = 0x1
                var b1: Byte = 0xFF
                var b2: Byte = 0x0
            }
        """

        transpileAndAssert(input, output)
    }

    @Test
    fun `should convert char declaration`() {
        val input = """
            class Test {
                char c0 = 'a';
                char c1;
            }
        """

        val output = """
            class Test {
                var c0: Char = 'a'
                var c1: Char = 0.toChar()
            }
        """

        transpileAndAssert(input, output)
    }

    @Test
    fun `should convert int declaration`() {
        val input = """
            class Test {
                int v0 = 1;
                int v1;
            }
        """

        val output = """
            class Test {
                var v0: Int = 1
                var v1: Int = 0
            }
        """

        transpileAndAssert(input, output)
    }

    @Test
    fun `should convert long declaration`() {
        val input = """
            class Test {
                long v0 = 1;
                long v1;
            }
        """

        val output = """
            class Test {
                var v0: Long = 1L
                var v1: Long = 0L
            }
        """

        transpileAndAssert(input, output)
    }

    @Test
    fun `should convert float declaration`() {
        val input = """
            class Test {
                float v0 = 1;
                float v1 = 1f;
                float v2 = 1F;
                float v3 = 0.1F;
                float v4;
            }
        """

        val output = """
            class Test {
                var v0: Float = 1F
                var v1: Float = 1F
                var v2: Float = 1F
                var v3: Float = 0.1F
                var v4: Float = 0F
            }
        """

        transpileAndAssert(input, output)
    }

    @Test
    fun `should convert double declaration`() {
        val input = """
            class Test {
                double v0 = 1;
                double v1 = 1d;
                double v2 = 1D;
                double v3 = 0.1;
                double v4 = 0.1d;
                double v5 = 0.1D;
                double v6;
            }
        """

        val output = """
            class Test {
                var v0: Double = 1.0
                var v1: Double = 1.0
                var v2: Double = 1.0
                var v3: Double = 0.1
                var v4: Double = 0.1
                var v5: Double = 0.1
                var v6: Double = 0.0
            }
        """

        transpileAndAssert(input, output)
    }
}