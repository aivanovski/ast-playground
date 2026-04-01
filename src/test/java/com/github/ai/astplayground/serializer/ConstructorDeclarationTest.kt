package com.github.ai.astplayground.serializer

import com.github.ai.astplayground.astDsl.AstBuilderDsl.javaAst
import com.github.ai.astplayground.astDsl.ParametersFactory.int
import com.github.ai.astplayground.transpileAndAssert
import org.junit.jupiter.api.Test

class ConstructorDeclarationTest {

    @Test
    fun `should support constructor declaration`() {
        transpileAndAssert(
            input = javaAst {
                `class`("Test") {
                    constructor()
                    constructor(
                        int("i0"),
                        int("i1")
                    )
                }
            },
            expected = """
                class Test {
                    constructor() {}
                    constructor(i0: Int, i1: Int) {}
                }
            """
        )
    }
}