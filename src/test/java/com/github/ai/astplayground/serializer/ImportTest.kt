package com.github.ai.astplayground.serializer

import com.github.ai.astplayground.astDsl.AstBuilderDsl.javaAst
import com.github.ai.astplayground.transpileAndAssert
import org.junit.jupiter.api.Test

class ImportTest {

    @Test
    fun `should support import declaration`() {
        transpileAndAssert(
            input = javaAst {
                import("java.util.List")
            },
            expected = "import java.util.List"
        )
    }
}
