package org.mikrograd.core

fun <T> printComputeGraph(node: ComputeNode<T>, indent: String = ""): String =
    when (node) {
        is ValueNode -> "${indent}Value(${node.evaluate()}) [id=${node.id}]\n"
        is AddNode -> buildString {
            append("${indent}Add [id=${node.id}]\n")
            node.inputs.forEach { append(printComputeGraph(it, "$indent  ")) }
        }

        is MultiplyNode -> buildString {
            append("${indent}Multiply [id=${node.id}]\n")
            node.inputs.forEach { append(printComputeGraph(it, "$indent  ")) }
        }

        is DifferentiableFunctionNode -> buildString {
            append("${indent}${node}\n")
            node.inputs.forEach { append(printComputeGraph(it, "$indent  ")) }
        }

        else -> "${indent}Unknown [id=${node.id}]\n"
    }
