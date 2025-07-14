package org.mikrograd.diff

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ForwardValueTest {
    @Test
    fun testForwardValueOperations() {
        // Create ForwardValue instances
        val x = ForwardValue(2.0)
        val y = ForwardValue(3.0)
        
        // Test basic operations
        val z = x + y
        assertEquals(5.0, z.data)
        assertIs<ForwardValue>(z)
        
        val w = x * y
        assertEquals(6.0, w.data)
        assertIs<ForwardValue>(w)
        
        val v = x.pow(2.0)
        assertEquals(4.0, v.data)
        assertIs<ForwardValue>(v)
        
        val u = x.relu()
        assertEquals(2.0, u.data)
        assertIs<ForwardValue>(u)
    }
    
    @Test
    fun testValueFactory() {
        // Test creating ForwardValue with requiresGrad = false
        val x = ValueFactory.create(2.0, requiresGrad = false)
        assertIs<ForwardValue>(x)
        
        // Test creating BackwardValue with requiresGrad = true
        val y = ValueFactory.create(3.0, requiresGrad = true)
        assertIs<BackwardValue>(y)
        
        // Test operations with mixed types
        val z = x + y
        assertIs<BackwardValue>(z)
        assertEquals(5.0, z.data)
        
        // Verify that backward pass works with BackwardValue
        val w = y * y
        assertIs<BackwardValue>(w)
        assertEquals(9.0, w.data)
        w.backward()
        assertEquals(6.0, (y as BackwardValue).grad)
    }
    
    @Test
    fun testMemoryUsageComparison() {
        // This is a simple demonstration of memory usage difference
        // In a real test, we would use a proper memory measurement tool
        
        // Create a large number of values
        val count = 1000
        
        // Create ForwardValue instances
        val forwardValues = List(count) { ForwardValue(it.toDouble()) }
        val forwardResult = forwardValues.reduce { acc, value -> acc + value }
        assertEquals(count * (count - 1) / 2.0, forwardResult.data)
        
        // Create BackwardValue instances
        val backwardValues = List(count) { BackwardValue(it.toDouble()) }
        val backwardResult = backwardValues.reduce { acc, value -> acc + value }
        assertEquals(count * (count - 1) / 2.0, backwardResult.data)
        
        // In a real test, we would measure and compare memory usage here
        // For now, we just verify that the computation is correct
    }
}