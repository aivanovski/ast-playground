package com.github.ai.astplayground.serializer

import com.github.ai.astplayground.astDsl.AstBuilderDsl.javaAst
import com.github.ai.astplayground.transpileAndAssert
import org.junit.jupiter.api.Test

class PackageTest {

    @Test
    fun `should support package declaration`() {
        transpileAndAssert(
            input = javaAst {
                `package`("com.example.name")
            },
            expected = "package com.example.name"
        )
    }
}