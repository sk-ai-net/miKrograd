package org.mikrograd.diff

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ForwardValueTest {
    @Test
    fun testForwardValueOperations() {
        // Create ForwardValue instances
        val x = ForwardPassNode(2.0)
        val y = ForwardPassNode(3.0)
        
        // Test basic operations
        val z = x + y
        assertEquals(5.0, z.data)
        assertIs<ForwardPassNode>(z)
        
        val w = x * y
        assertEquals(6.0, w.data)
        assertIs<ForwardPassNode>(w)
        
        val v = x.pow(2.0)
        assertEquals(4.0, v.data)
        assertIs<ForwardPassNode>(v)
        
        val u = x.relu()
        assertEquals(2.0, u.data)
        assertIs<ForwardPassNode>(u)
    }
    

    @Test
    fun testMemoryUsageComparison() {
        // This is a simple demonstration of memory usage difference
        // In a real test, we would use a proper memory measurement tool
        
        // Create a large number of values
        val count = 1000
        
        // Create ForwardValue instances
        val forwardPassNodes = List(count) { ForwardPassNode(it.toDouble()) }
        val forwardResult = forwardPassNodes.reduce { acc, value -> acc + value }
        assertEquals(count * (count - 1) / 2.0, forwardResult.data)
        
        // Create BackwardValue instances
        val values = List(count) { BackpropNode(it.toDouble()) }
        val backwardResult = values.reduce { acc, value -> acc + value }
        assertEquals(count * (count - 1) / 2.0, backwardResult.data)
        
        // In a real test, we would measure and compare memory usage here
        // For now, we just verify that the computation is correct
    }
}