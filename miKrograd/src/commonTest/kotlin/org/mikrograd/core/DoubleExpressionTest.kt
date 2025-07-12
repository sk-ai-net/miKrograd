package org.mikrograd.core

import kotlin.test.Test
import kotlin.test.assertEquals

class DoubleExpressionTest {
    @Test
    fun testBuildDoubleExpression() {
        // Build the compute graph for the expression 3.0*4.0
        val computeNode = buildDoubleExpression()
        
        // Evaluate the compute graph
        val result = computeNode.evaluate()
        
        // Check that the result is 12.0
        assertEquals(12.0, result)
        
        // Check that the compute graph has the expected structure
        assert(computeNode is MultiplyNode)
        assertEquals(2, computeNode.inputs.size)
        assert(computeNode.inputs[0] is ValueNode)
        assert(computeNode.inputs[1] is ValueNode)
        assertEquals(3.0, (computeNode.inputs[0] as ValueNode<Double>).evaluate())
        assertEquals(4.0, (computeNode.inputs[1] as ValueNode<Double>).evaluate())
    }
}