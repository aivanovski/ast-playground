package com.github.ai.astplayground.serializer

import com.github.ai.astplayground.transpileJavaAndAssert
import org.junit.jupiter.api.Test

class MethodDeclarationTest {

    @Test
    fun `should support method declaration`() {
        transpileJavaAndAssert(
            input = """
                class Test {
                    void m0() {
                    }
                    Object m1() {
                        return null;
                    }
                    void m2(int i0, int i1) {
                    }
                    int m3() {
                        return 1;
                    }
                    static void sm0() {
                    }
                }
            """,
            expected = """
                class Test {
                    fun m0() {}
                    fun m1(): Object? {
                        return null
                    }
                    fun m2(i0: Int, i1: Int) {}
                    fun m3(): Int {
                        return 1
                    }
                    companion object {
                        fun sm0() {}
                    }
                }
            """
        )
    }
}