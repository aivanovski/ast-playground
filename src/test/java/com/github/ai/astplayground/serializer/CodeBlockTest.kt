package com.github.ai.astplayground.serializer

import com.github.ai.astplayground.transpileJavaAndAssert
import org.junit.jupiter.api.Test

class CodeBlockTest {

    @Test
    fun `should support method body`() {
        transpileJavaAndAssert(
            input = """
                class Test {
                    void m0() {
                        System.out.println("Hello");
                    }
                    String m1() {
                        return "abc123";
                    }
                    String m2() {
                        String s0 = "abc";
                        String s1 = "123";
                        return s0 + s1;
                    }
                    int sum(int a, int b) {
                        return a + b;
                    }
                    void m3() {
                        int i = sum(1, 2);
                    }
                }
            """,
            expected = """
                class Test {
                    fun m0() {
                        System.out.println("Hello")
                    }
                    fun m1(): String? {
                        return "abc123"
                    }
                    fun m2(): String? {
                        var s0: String = "abc"
                        var s1: String = "123"
                        return s0 + s1
                    }
                    fun sum(a: Int, b: Int): Int {
                        return a + b
                    }
                    fun m3() {
                        var i: Int = sum(1, 2)
                    }
                }
            """
        )
    }
}
