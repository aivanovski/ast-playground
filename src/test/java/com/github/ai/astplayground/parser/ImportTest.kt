package com.github.ai.astplayground.parser

import com.github.ai.astplayground.astDsl.AstBuilderDsl.buildAst
import com.github.ai.astplayground.parseAndAssert
import org.junit.jupiter.api.Test

class ImportTest {

    @Test
    fun `should support import declaration`() {
        parseAndAssert(
            input = """
                import java.util.List;
            """,
            expected = buildAst {
                import("java.util.List")
            }
        )
    }
}