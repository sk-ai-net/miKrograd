package org.mikrograd.core

/**
 * Builds a compute graph for the double expression 3.0*4.0.
 *
 * This function creates a computation graph that represents the multiplication
 * of two constant values: 3.0 and 4.0.
 *
 * @return A ComputeNode representing the expression 3.0*4.0.
 */
fun buildDoubleExpression(): ComputeNode<Double> {
    // Create ValueNodes for the constants 3.0 and 4.0 with custom IDs
    val value1 = ValueNode(3.0).withId("const_3.0")
    val value2 = ValueNode(4.0).withId("const_4.0")

    // Create a MultiplyNode with a lambda that multiplies two Double values
    // and set a custom ID for it
    val multiplyNode = MultiplyNode<Double> { a, b -> a * b }.withId("multiply_3.0_4.0")

    // Add the ValueNodes as inputs to the MultiplyNode
    multiplyNode.inputs.add(value1)
    multiplyNode.inputs.add(value2)

    // Return the MultiplyNode as the root of the compute graph
    return multiplyNode
}
