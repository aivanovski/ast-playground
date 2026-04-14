package com.github.ai.astplayground.serializer

import com.github.ai.astplayground.transpileJavaAndAssert
import org.junit.jupiter.api.Test

class PackageTest {

    @Test
    fun `should support package declaration`() {
        transpileJavaAndAssert(
            input = """
                package com.example.name;
            """,
            expected = """
                package com.example.name
            """,
        )
    }
}