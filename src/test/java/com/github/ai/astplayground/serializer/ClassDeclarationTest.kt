package com.github.ai.astplayground.serializer

import com.github.ai.astplayground.astDsl.AstBuilderDsl.javaAst
import com.github.ai.astplayground.transpileAndAssert
import org.junit.jupiter.api.Test

class ClassDeclarationTest {

    @Test
    fun `should support class declaration`() {
        transpileAndAssert(
            input = javaAst {
                `class`("Test")
            },
            expected = "class Test"
        )
    }
}