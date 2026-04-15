package com.github.ai.astplayground.transpiler.serializer.model

fun KTypeReference.isPrimitiveByte() =
    name == "Byte"

fun KTypeReference.isPrimitiveChar() =
    name == "Char"

fun KTypeReference.isPrimitiveInt() =
    name == "Int"

fun KTypeReference.isPrimitiveLong() =
    name == "Long"

fun KTypeReference.isPrimitiveFloat() =
    name == "Float"

fun KTypeReference.isPrimitiveDouble() =
    name == "Double"

fun KTypeReference.isPrimitiveBoolean() =
    name == "Boolean"

fun KTypeReference.isUnit() =
    name == "Unit"