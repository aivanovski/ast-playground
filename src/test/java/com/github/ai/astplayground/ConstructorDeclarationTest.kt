package com.github.ai.astplayground

import org.junit.jupiter.api.Test

class ConstructorDeclarationTest {

    @Test
    fun `should convert constructor declaration`() {
        val input = """
            class Test {
                int i0;
                int i1;
                Test() {
                }
                Test(int i0, int i1) {
                    this.i0 = i0;
                    this.i1 = i1;
                }
            }
        """

        val output = """
            class Test {
                var i0: Int = 0
                var i1: Int = 0
                constructor() {
                }
                constructor(i0: Int, i1: Int) {
                    this.i0 = i0
                    this.i1 = i1
                }
            }
        """

        transpileAndAssert(input, output)
    }
}