package com.github.ai.astplayground.transpiler.transformer

import com.github.ai.astplayground.transpiler.transformer.model.KotlinAstNode

data class IRNode(
    val parent: IRNode?,
    val node: KotlinAstNode,
    val nodes: MutableList<IRNode>
)