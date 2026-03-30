package com.github.ai.astplayground

import com.github.ai.astplayground.assertionDsl.AstBuilderDsl.buildAst
import com.github.ai.astplayground.assertionDsl.FieldFactory
import com.github.ai.astplayground.assertionDsl.IdentifierFactory.field
import com.github.ai.astplayground.assertionDsl.ParametersFactory
import com.github.ai.astplayground.assertionDsl.TypeReferenceFactory.void
import org.junit.jupiter.api.Test

class AssignmentTest {

    @Test
    fun `should support assignment to field`() {
        parseAndAssert(
            input = """
                class Test {
                    int i0;
                    void m0(int p0) {
                        this.i0 = p0;
                    }
                }
            """,
            expected = buildAst {
                `class`("Test") {
                    field(FieldFactory.int("i0"))

                    method(
                        "m0",
                        ParametersFactory.int("p0"),
                        returns = void()
                    ) {
                        assign("this" field "i0", "p0")
                    }
                }
            }
        )
    }
}