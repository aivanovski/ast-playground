package com.github.ai.astplayground

import org.junit.jupiter.api.Test

class ConstructorInvocationTest {

    @Test
    fun `should convert constructor invocation`() {
        val input = """
            class Test {
                void m0() {
                    Test0 t0 = new Test0(1, 2);
                }
                class Test0 {
                    Test0(int i0, int i1) {
                    }
                }
            }
        """

        val output = """
            class Test {
                fun m0() {
                    var t0: Test0? = Test0(1, 2)
                }
                class Test0 {
                    constructor(i0: Int, i1: Int) {
                    }
                }
            }
        """

        transpileAndAssert(input, output)
    }
}