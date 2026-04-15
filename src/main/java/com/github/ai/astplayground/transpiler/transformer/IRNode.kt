package com.github.ai.astplayground.transpiler.transformer

import com.github.ai.astplayground.transpiler.parser.model.JavaAstNode
import com.github.ai.astplayground.transpiler.serializer.model.KotlinAstNode

data class IRNode(
    val jastNode: JavaAstNode,
    val resolvedNode: KotlinAstNode?,
    val nodes: List<IRNode>
)