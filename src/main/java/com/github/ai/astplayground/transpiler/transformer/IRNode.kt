package com.github.ai.astplayground.transpiler.transformer

import com.github.ai.astplayground.transpiler.parser.model.JTypeReference
import com.github.ai.astplayground.transpiler.transformer.model.KotlinAstNode

data class IRNode(
    val parent: IRNode?,
    val node: KotlinAstNode,
    val resolvedType: JTypeReference?,
    val nodes: MutableList<IRNode>
)