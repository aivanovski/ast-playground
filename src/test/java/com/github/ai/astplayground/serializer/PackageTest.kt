package com.github.ai.astplayground.serializer

import com.github.ai.astplayground.astDsl.AstBuilderDsl.buildAst
import com.github.ai.astplayground.serializeAndAssert
import org.junit.jupiter.api.Test

class PackageTest {

    @Test
    fun `should support package declaration`() {
        serializeAndAssert(
            input = buildAst {
                `package`("com.example.name")
            },
            expected = "package com.example.name"
        )
    }
}