package com.github.ai.astplayground.serializer

import com.github.ai.astplayground.transpileJavaAndAssert
import org.junit.jupiter.api.Test

class ImportTest {

    @Test
    fun `should support import declaration`() {
        transpileJavaAndAssert(
            input = """
                import java.util.List;
            """,
            expected = """
                import java.util.List
            """
        )
    }
}
