package com.github.ai.astplayground.extension

import com.github.ai.astplayground.model.VariableType

fun VariableType.getName(): String {
    val loweredName = this.name.lowercase()
    return loweredName[0].uppercase() + loweredName.substring(1, loweredName.length)
}