package com.example

import org.mikrograd.diff.DiffValue
import org.mikrograd.diff.grad
import org.mikrograd.diff.ksp.Mikrograd

@Mikrograd
fun calcMain() {
    val result: DiffValue<Double> = grad {
        3.0 * 4.0
    }
    print(result.derivative)
}


fun main() {
    calcMain()
}

