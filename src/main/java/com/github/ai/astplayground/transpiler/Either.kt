package com.aivanouski.playground.core

sealed interface Either<out E, out A> {
    data class Left<out E>(
        val value: E,
    ) : Either<E, Nothing>

    data class Right<out A>(
        val value: A,
    ) : Either<Nothing, A>

    companion object {
        fun <E> left(value: E): Either<E, Nothing> = Left(value)

        fun <A> right(value: A): Either<Nothing, A> = Right(value)
    }
}

interface EitherRaise<E> {
    fun raise(error: E): Nothing

    fun <A> Either<E, A>.bind(): A = when (this) {
        is Either.Left -> raise(value)
        is Either.Right -> value
    }
}

fun <E, A> either(block: EitherRaise<E>.() -> A): Either<E, A> {
    // val raise = DefaultEitherRaise<E>()
    //
    // return try {
    //     Either.Right(raise.block())
    // } catch (error: RaiseCancellationException) {
    //     @Suppress("UNCHECKED_CAST")
    //     Either.Left(error.error as E)
    // }
    throw IllegalStateException("")
}

class DefaultEitherRaise<E> : EitherRaise<E> {
    override fun raise(error: E): Nothing = throw RaiseCancellationException(error)
}

class RaiseCancellationException(
    val error: Any?,
) : RuntimeException(null, null, false, false)
