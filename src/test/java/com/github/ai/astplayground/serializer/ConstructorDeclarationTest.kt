package com.github.ai.astplayground.serializer

import com.github.ai.astplayground.transpileJavaAndAssert
import org.junit.jupiter.api.Test

class ConstructorDeclarationTest {

    @Test
    fun `should support constructor declaration`() {
        transpileJavaAndAssert(
            input = """
                class Test {
                    Test() {
                    }
                    Test(int i0, int i1) {
                    }
                }
            """,
            expected = """
                class Test {
                    constructor() {}
                    constructor(i0: Int, i1: Int) {}
                }
            """
        )
    }
}