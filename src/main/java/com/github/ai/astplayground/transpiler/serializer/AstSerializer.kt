package com.github.ai.astplayground.transpiler.serializer

import com.github.ai.astplayground.transpiler.model.JavaAstNode

interface AstSerializer {
    fun serialize(nodes: List<JavaAstNode>): String
}