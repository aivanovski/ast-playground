package com.github.ai.astplayground.extension

import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration

const val COMPANION_OBJECT = "CompanionObject"

fun ClassOrInterfaceDeclaration.getOrCreateCompanionObject(): ClassOrInterfaceDeclaration {
    val existing = this.findAll(ClassOrInterfaceDeclaration::class.java)
        .firstOrNull { type ->
            type.nameAsString == COMPANION_OBJECT
        }

    return if (existing == null) {
        val created = ClassOrInterfaceDeclaration(NodeList(), false, COMPANION_OBJECT)
        this.addMember(created)
        created
    } else {
        existing
    }
}


fun NodeList<Modifier>.hasStaticModifier(): Boolean {
    return this.any { modifier ->
        modifier.keyword == Modifier.Keyword.STATIC
    }
}
