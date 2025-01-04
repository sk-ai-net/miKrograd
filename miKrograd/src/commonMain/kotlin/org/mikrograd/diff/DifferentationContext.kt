package org.mikrograd.diff

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Automatic Differentiation context class.
 */
abstract class DifferentiationContext<T> {
    abstract operator fun DerivativeValueHolder<T>.plus(that: ValueHolder<T>): DerivativeValueHolder<T>
    abstract fun backprop()
}

class DifferentiationContextImplDouble : DifferentiationContext<Double>() {
    override fun DerivativeValueHolder<Double>.plus(that: ValueHolder<Double>): DerivativeValueHolder<Double> =
        DoubleValueDerivative(this.value + that.value, 0.0, "+")

    override fun backprop() {
        //
    }
}

public inline fun <R> grad(block: () -> R):  DiffValue<R> {
    return  block()
}


/*
fun grad(body: DifferentiationContext<Double>.() -> DerivativeValueHolder<Double>): DerivativeValueHolder<Double> =
    DifferentiationContextImplDouble().run {
        val result: DerivativeValueHolder<Double> = body()
        result.derivative = 1.0 // computing derivative w.r.t result
        backprop()
        result
    }


 */


