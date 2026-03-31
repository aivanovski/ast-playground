package com.github.ai.astplayground.serializer

import com.github.ai.astplayground.astDsl.AstBuilderDsl.buildAst
import com.github.ai.astplayground.serializeAndAssert
import org.junit.jupiter.api.Test

class ClassDeclarationTest {

    @Test
    fun `should support class declaration`() {
        serializeAndAssert(
            input = buildAst {
                `class`("Test")
            },
            expected = "class Test"
        )
    }
}