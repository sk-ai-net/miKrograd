package com.example

import org.mikrograd.diff.BackwardValue
import org.mikrograd.diff.ksp.ComputationMode
import org.mikrograd.diff.ksp.Mikrograd

@Mikrograd(ComputationMode.INFERENCE)
fun testExpr() {
    3.0 * 4.0 + (7.0 + 3.0)
}

@Mikrograd(ComputationMode.TRAINING)
fun testBackExpr() {
    3.0 * 4.0 + (7.0 + 3.0)
}

fun main(args: Array<String>) {
    // Test the KSP-generated functions
    val a = testExprGenerated()
    println("KSP-generated inference function:")
    println("Is BackwardValue: ${a is BackwardValue}")
    println("Data: ${a.data}")
    println()

    val b = testBackExprGenerated()
    println("KSP-generated training function:")
    println("Is BackwardValue: ${b is BackwardValue}")
    println("Data: ${b.data}")
    b.backward()
    if (b is BackwardValue) {
        println("Gradient: ${(b as BackwardValue).grad}")
    }
    println()
}
