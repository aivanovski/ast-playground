package com.github.ai.astplayground

import org.junit.jupiter.api.Test

class LoopsTest {

    @Test
    fun `should convert for loop`() {
        val input = """
            import java.util.Arrays;
            import java.util.List;
            class Test {
                void m0() {
                    List<Integer> nums = Arrays.asList(1, 2, 3);
                    for (int i : nums) {
                        System.out.println(nums.get(i));
                    }
                }
            }
        """

        val output = """
            import java.util.Arrays
            import java.util.List
            class Test {
                fun m0() {
                    var nums: List<Integer>? = Arrays.asList(1, 2, 3)
                    for (i in nums) {
                        System.out.println(nums?.get(i))
                    }
                }
            }
        """

        transpileAndAssert(input, output)
    }
}