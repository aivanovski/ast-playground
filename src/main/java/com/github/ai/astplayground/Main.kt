package com.github.ai.astplayground

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.visitor.VoidVisitorAdapter


object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val sourceCode = Main.javaClass.getResourceAsStream("/Test.java")
            .reader()
            .readText()
            .trim()

        println("=========== SOURCE ============")
        println(sourceCode)
        println("=======================")

        val parseResult = JavaParser().parse(sourceCode)
        if (parseResult.isSuccessful) {
            parseResult.result.get().accept(KotlinCodeGenerator(), null)
        }
    }

    class KotlinCodeGenerator : VoidVisitorAdapter<Void?>() {

        override fun visit(n: ClassOrInterfaceDeclaration, arg: Void?) {
            println("class " + n.nameAsString + " {")
            super.visit(n, arg)
            println("}")
        }

        override fun visit(n: MethodDeclaration, arg: Void?) {
            println("    fun " + n.nameAsString + "() {")
            super.visit(n, arg)
            println("    }")
        }
    }
}


