package com.github.ai.astplayground.serializer

import com.github.ai.astplayground.astDsl.AstBuilderDsl.buildAst
import com.github.ai.astplayground.serializeAndAssert
import org.junit.jupiter.api.Test

class ImportTest {

    @Test
    fun `should support import declaration`() {
        serializeAndAssert(
            input = buildAst {
                import("java.util.List")
            },
            expected = "import java.util.List"
        )
    }
}
