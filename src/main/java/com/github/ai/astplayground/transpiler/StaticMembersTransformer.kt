package com.github.ai.astplayground.transpiler

import com.github.ai.astplayground.extension.getOrCreateCompanionObject
import com.github.ai.astplayground.extension.hasStaticModifier
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration

class StaticMembersTransformer {

    fun transform(node: CompilationUnit): CompilationUnit {
        node.findAll(MethodDeclaration::class.java).forEach { n ->
            val isStatic = n.modifiers.hasStaticModifier()

            if (isStatic) {
                val parent = n.parentNode.get() as ClassOrInterfaceDeclaration
                val companion = parent.getOrCreateCompanionObject()
                val method = companion.addMethod(n.nameAsString)

                method.setBody(n.body.get())
                method.setParameters(n.parameters)
                method.setType(n.type)

                parent.remove(n)
            }
        }

        return node
    }
}