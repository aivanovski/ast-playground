package com.github.ai.astplayground

import org.junit.jupiter.api.Test

class MethodInvocationTest {

    @Test
    fun `should convert class instance variable as unsafe`() {
        val input = """
            import java.util.ArrayList;
            class Test {
                ArrayList<Integer> nums = new ArrayList<>();
                void m0() {
                    nums.add(1);
                }
            }
        """

        val output = """
            import java.util.ArrayList
            class Test {
                var nums: ArrayList<Integer>? = ArrayList()
                fun m0() {
                    nums?.add(1)
                }
            }
        """

        transpileAndAssert(input, output)
    }

    @Test
    fun `should convert local variable as unsafe`() {
        val input = """
            import java.util.ArrayList;
            class Test {
                void m0() {
                    ArrayList<Long> nums = new ArrayList<>();
                    nums.add(1l);
                }
            }
        """

        val output = """
            import java.util.ArrayList
            class Test {
                fun m0() {
                    var nums: ArrayList<Long>? = ArrayList()
                    nums?.add(1L)
                }
            }
        """

        transpileAndAssert(input, output)
    }

    @Test
    fun `should convert standard library static method invocation`() {
        val input = """
            class Test {
                void m0() {
                    System.out.println("abc");
                    Math.abs(1);
                }
            }
        """

        val output = """
            class Test {
                fun m0() {
                    System.out.println("abc")
                    Math.abs(1)
                }
            }
        """

        transpileAndAssert(input, output)
    }
}