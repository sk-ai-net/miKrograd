package org.mikrograd.utils

import org.mikrograd.diff.AutoDiffNode
import org.mikrograd.diff.BackpropNode

/**
 * Simple DOT graph representation for visualization
 */
data class DotGraph(val content: String) {
    fun toFile(filename: String) {
        // For now, just return the content - can be extended to write to file
        println("DOT Graph content for $filename:")
        println(content)
    }
}

fun trace(root: AutoDiffNode): Pair<Set<AutoDiffNode>, Set<Pair<AutoDiffNode, AutoDiffNode>>> {
    val nodes = mutableSetOf<AutoDiffNode>()
    val edges = mutableSetOf<Pair<AutoDiffNode, AutoDiffNode>>()

    fun build(v: AutoDiffNode) {
        if (v !in nodes) {
            nodes.add(v)
            for (child in v.children) {
                edges.add(child to v)
                build(child)
            }
        }
    }

    build(root)
    return nodes to edges
}

fun drawDot(root: AutoDiffNode, withGradient: Boolean = false, rankdir: String = "LR"): DotGraph {
    require(rankdir in listOf("LR", "TB"))

    val (nodes, edges) = trace(root)
    val dotContent = StringBuilder()

    dotContent.appendLine("digraph {")
    dotContent.appendLine("    rankdir=$rankdir;")

    // Add nodes
    for (n in nodes) {
        val nodeId = n.hashCode().toString()
        
        val labelContent = when {
            withGradient && n is BackpropNode -> {
                "${n.label} | value %.4f | grad %.4f".format(n.data, n.grad)
            }
            else -> {
                "${n.label} | value %.4f".format(n.data)
            }
        }
        
        dotContent.appendLine("    $nodeId [label=\"$labelContent\", shape=record];")
        
        // Add operation node if operation exists
        if (n.op.isNotEmpty()) {
            val opId = "${n.hashCode()}${n.op}"
            dotContent.appendLine("    \"$opId\" [label=\"${n.op}\"];")
            dotContent.appendLine("    \"$opId\" -> $nodeId;")
        }
    }

    // Add edges
    for ((n1, n2) in edges) {
        val n1Id = n1.hashCode().toString()
        val n2Target = if (n2.op.isNotEmpty()) {
            "\"${n2.hashCode()}${n2.op}\""
        } else {
            n2.hashCode().toString()
        }
        dotContent.appendLine("    $n1Id -> $n2Target;")
    }

    dotContent.appendLine("}")
    
    return DotGraph(dotContent.toString())
}