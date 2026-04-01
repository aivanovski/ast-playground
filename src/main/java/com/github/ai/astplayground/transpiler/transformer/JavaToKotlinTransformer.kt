package com.github.ai.astplayground.transpiler.transformer

import com.github.ai.astplayground.transpiler.parser.model.JavaAstNode
import com.github.ai.astplayground.transpiler.serializer.model.KotlinAstNode

class JavaToKotlinTransformer {

    fun transform(javaAst: List<JavaAstNode>): List<KotlinAstNode> {
        return javaAst.map { node ->
            when (node) {
                is JavaAstNode.Package -> KotlinAstNode.Package(
                    name = node.name
                )

                is JavaAstNode.Import -> KotlinAstNode.Import(
                    name = node.name,
                    isStatic = node.isStatic,
                    isAsterisk = node.isAsterisk
                )

                is JavaAstNode.Class -> KotlinAstNode.Class(
                    name = node.name,
                    modifiers = node.modifiers,
                    fields = node.fields,
                    constructors = node.constructors,
                    methods = node.methods
                )
            }
        }
    }
}
