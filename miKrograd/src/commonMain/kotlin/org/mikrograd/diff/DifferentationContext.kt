package org.mikrograd.diff

import org.mikrograd.core.ComputeNode
import org.mikrograd.core.ValueNode
import org.mikrograd.diff.ksp.Mikrograd
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Mikrograd
public fun <R> graph(block: () -> R): ComputeNode<R> {
    // The KSP processor will generate a function with the name of the caller function plus "_Generated"
    // We'll use reflection to find and call that function if it exists

    // Fall back to the default implementation
    return buildValueNode(block)
}

fun <R> buildValueNode(block: () -> R): ValueNode<R> {
    // This is a fallback implementation that will be used if the KSP processor hasn't generated a function
    val result = block()
    return ValueNode(result)
}



public fun <R> grad(block: () -> R): DiffValue<R> {
    return buildDiffValue(block)
}

fun <R> buildDiffValue(block: () -> R): DiffValue<R> {
    TODO("Not yet implemented")
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
