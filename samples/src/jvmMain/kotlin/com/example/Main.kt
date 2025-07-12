package com.example

import org.mikrograd.core.printComputeGraph
import org.mikrograd.diff.graph
import org.mikrograd.diff.ksp.Mikrograd

@Mikrograd
fun calcMain() {

    val compute = graph {
        3.0 * 4.0
    }
    println(printComputeGraph(compute))
    assert(compute.evaluate() == 7.0)

    /*
        val result = autodiff {
            3.0 * 4.0
        }
        print(result.derivative)

     */
}


fun main() {
    calcMain()
}

