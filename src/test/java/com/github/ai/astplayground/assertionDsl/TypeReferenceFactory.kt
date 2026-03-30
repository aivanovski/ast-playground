package com.github.ai.astplayground.assertionDsl

import com.github.ai.astplayground.transpiler.model.TypeReference
import com.github.ai.astplayground.transpiler.model.TypeReferenceKind

object TypeReferenceFactory {

    fun String.asType() = TypeReference(
        name = this,
        kind = TypeReferenceKind.DECLARED,
        typeArguments = emptyList()
    )

    infix fun String.parameterizedWith(parameterizedType: String) = parameterizedType(
        name = this,
        parameterizedWith = parameterizedType
    )

    fun parameterizedType(
        name: String,
        parameterizedWith: String
    ) = TypeReference(
        name = name,
        kind = TypeReferenceKind.DECLARED,
        typeArguments = listOf(type(parameterizedWith))
    )

    fun type(
        name: String,
        vararg typeArguments: TypeReference
    ) = TypeReference(
        name = name,
        kind = TypeReferenceKind.DECLARED,
        typeArguments = typeArguments.toList()
    )

    fun string() = NonPrimitiveTypes.STRING
    fun void() = NonPrimitiveTypes.VOID
    fun boolean() = PrimitiveTypes.BOOLEAN
    fun byte() = PrimitiveTypes.BYTE
    fun char() = PrimitiveTypes.CHAR
    fun short() = PrimitiveTypes.SHORT
    fun int() = PrimitiveTypes.INT
    fun long() = PrimitiveTypes.LONG
    fun float() = PrimitiveTypes.FLOAT
    fun double() = PrimitiveTypes.DOUBLE
}