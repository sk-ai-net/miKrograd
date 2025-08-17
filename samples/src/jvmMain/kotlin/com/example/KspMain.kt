package com.example

import org.mikrograd.diff.BackpropNode
import org.mikrograd.diff.ksp.ComputationMode
import org.mikrograd.diff.ksp.Mikrograd
import org.mikrograd.utils.drawDot

@Mikrograd(ComputationMode.INFERENCE)
fun testExpr() {
    3.0 * 5.0 + (7.0 + 3.0)
}

@Mikrograd(ComputationMode.TRAINING)
fun testBackExpr() {
    3.0 * 5.0 + (7.0 + 3.0)
}

fun main(args: Array<String>) {
    // Test the KSP-generated functions
    val a = testExprGenerated()
    println("KSP-generated inference function:")
    println("Is BackwardValue: ${a is BackpropNode}")
    println("Data: ${a.data}")
    println()

    val b = testBackExprGenerated()
    println("KSP-generated training function:")
    println("Is BackwardValue: ${b is BackpropNode}")
    println("Data: ${b.data}")
    b.backward()
    if (b is BackpropNode) {
        println("Gradient: ${b.grad}")
    }
    println()
    val graph = drawDot(b, )
    println(graph)
}
