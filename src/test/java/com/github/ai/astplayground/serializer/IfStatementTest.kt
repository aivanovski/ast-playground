package com.github.ai.astplayground.serializer

import com.github.ai.astplayground.transpileJavaAndAssert
import org.junit.jupiter.api.Test

class IfStatementTest {

    @Test
    fun `should support if statements`() {
        transpileJavaAndAssert(
            input = """
                class Test {
                    void m0(int i) {
                        if (true) {
                            System.out.println("Hello");
                        }
                        if (true && false) {
                            System.out.println("Hello");
                        }
                        if (true || false) {
                            System.out.println("Hello");
                        }
                        if (i == 1) {
                            System.out.println("Hello");
                        }
                    }
                }
            """,
            expected = """
                class Test {
                    fun m0(i: Int) {
                        if (true) {
                            System.out.println("Hello")
                        }
                        if (true && false) {
                            System.out.println("Hello")
                        }
                        if (true || false) {
                            System.out.println("Hello")
                        }
                        if (i == 1) {
                            System.out.println("Hello")
                        }
                    }
                }
            """
        )
    }
}
