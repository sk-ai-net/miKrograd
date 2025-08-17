package org.mikrograd.utils

import org.mikrograd.diff.BackpropNode
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertContains

class GraphvizTest {
    
    @Test
    fun testDrawDotBasicFunctionality() {
        val a = BackpropNode(-4.0)
        a.label = "a"
        val b = BackpropNode(2.0)
        b.label = "b"
        val c = a + b
        c.label = "c"
        
        val dotGraph = drawDot(c, withGradient = false)
        
        // Verify that the DOT content is generated
        assertTrue(dotGraph.content.isNotEmpty())
        assertContains(dotGraph.content, "digraph")
        assertContains(dotGraph.content, "rankdir=LR")
    }
    
    @Test
    fun testDrawDotWithGradients() {
        val a = BackpropNode(-4.0)
        a.label = "a" 
        val b = BackpropNode(2.0)
        b.label = "b"
        val c = a + b
        c.label = "c"
        
        // Compute gradients
        c.backward()
        
        val dotGraph = drawDot(c, withGradient = true)
        
        // Verify gradient information is included
        assertTrue(dotGraph.content.isNotEmpty())
        assertContains(dotGraph.content, "grad")
    }
    
    @Test
    fun testTraceFunction() {
        val a = BackpropNode(-4.0)
        val b = BackpropNode(2.0)
        val c = a + b
        val d = c * a
        
        val (nodes, edges) = trace(d)
        
        // Should have nodes for a, b, c, and d
        assertTrue(nodes.size >= 4)
        // Should have edges connecting the computation graph
        assertTrue(edges.isNotEmpty())
    }
}