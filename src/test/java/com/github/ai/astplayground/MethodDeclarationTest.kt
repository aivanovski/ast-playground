package com.github.ai.astplayground

import org.junit.jupiter.api.Test

class MethodDeclarationTest {

    @Test
    fun `should convert simple method`() {
        val input = """
            class Test {
                void m0() {
                }
            }
        """

        val output = """
            class Test {
                fun m0() {
                }
            }
        """

        transpileAndAssert(input, output)
    }

    @Test
    fun `should convert method returning object`() {
        val input = """
            class Test {
                Object m0() {
                    return null;
                }
            }
        """

        val output = """
            class Test {
                fun m0(): Object? {
                    return null
                }
            }
        """

        transpileAndAssert(input, output)
    }

    @Test
    fun `should convert method with arguments`() {
        val input = """
            class Test {
                void m0(String s0, String s1) {
                }
            }
        """

        val output = """
            class Test {
                fun m0(s0: String?, s1: String?) {
                }
            }
        """

        transpileAndAssert(input, output)
    }

    @Test
    fun `should convert method returning primitive type`() {
        listOf(
            "boolean" to "false",
            "byte" to "0x0",
            "char" to "'a'",
            "int" to "1",
            "long" to "1L",
            "float" to "1F",
            "double" to "1.0d"
        ).forEach { (javaType, javaValue) ->
            val input = """
                class Test {
                    $javaType m0() {
                        return $javaValue;
                    }
                }
            """

            val kotlinType = javaType[0].uppercase() + javaType.substring(1)
            val kotlinValue = javaValue
                .replace("d", "")
                .replace("D", "")

            val output = """
                class Test {
                    fun m0(): $kotlinType {
                        return $kotlinValue
                    }
                }
            """

            transpileAndAssert(input, output)
        }
    }

    @Test
    fun `should convert static method`() {
        val input = """
            class Test {
                public static String m0(String s0) {
                    return "abc" + s0;
                }
                class Test0 {
                }
            }
        """

        val output = """
            class Test {
                class Test0 {
                }
                companion object {
                    fun m0(s0: String?): String? {
                        return "abc" + s0
                    }
                }
            }
        """

        transpileAndAssert(input, output)
    }
}