package org.mikrograd.diff.ksp

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ClassName

/**
 * Visitor for generating code that uses the differentiation context.
 * This visitor generates code that uses either ForwardValue or BackwardValue
 * based on the computation mode.
 */
class DifferentiationVisitor(private val mode: ComputationMode) : ComputeNodeVisitor<Double> {
    // Counter for generating unique variable names
    private var nodeCounter = 0

    override fun visitValueNode(value: Double, id: String): CodeBlock {
        val varName = generateNodeName("value")
        return CodeBlock.builder()
            .addStatement("val $varName = ${getConstructorByMode()}($value)")
            .build()
    }

    private fun getConstructorByMode(): String =
        if (mode == ComputationMode.INFERENCE) {
            "ForwardPassNode"
        } else {
            "BackpropNode"
        }


    override fun visitAddNode(left: CodeBlock, right: CodeBlock, id: String): CodeBlock {
        val leftVarName = extractLastVarName(left)
        val rightVarName = extractLastVarName(right)
        val varName = generateNodeName("add")

        return CodeBlock.builder()
            .add(left)
            .add(right)
            .addStatement("val $varName = $leftVarName + $rightVarName")
            .build()
    }

    override fun visitMultiplyNode(left: CodeBlock, right: CodeBlock, id: String): CodeBlock {
        val leftVarName = extractLastVarName(left)
        val rightVarName = extractLastVarName(right)
        val varName = generateNodeName("multiply")

        return CodeBlock.builder()
            .add(left)
            .add(right)
            .addStatement("val $varName = $leftVarName * $rightVarName")
            .build()
    }

    /**
     * Generate a unique variable name for a node.
     * @param prefix The prefix for the variable name
     * @return The generated variable name
     */
    private fun generateNodeName(prefix: String): String {
        return "${prefix}${nodeCounter++}"
    }

    /**
     * Extract the variable name from the last statement in a code block.
     * This is a simplistic implementation that assumes the last statement
     * in the code block is a variable declaration.
     * @param codeBlock The code block to extract from
     * @return The variable name
     */
    private fun extractLastVarName(codeBlock: CodeBlock): String {
        // Find the last variable declaration in the code block
        val statements = codeBlock.toString().trim().split("\n")
        for (i in statements.indices.reversed()) {
            val statement = statements[i]
            val match = Regex("val (\\w+)").find(statement)
            if (match != null) {
                return match.groupValues[1]
            }
        }

        // If no variable declaration is found, return a default name
        return "resultNode"
    }
}

/**
 * Visitor interface for traversing and evaluating compute nodes.
 * This interface defines methods for visiting different types of compute nodes
 * and generating the corresponding code.
 */
interface ComputeNodeVisitor<T> {
    /**
     * Visit a value node (leaf node with a constant value).
     * @param value The value of the node
     * @param id The ID of the node
     * @return The code block representing the compute node
     */
    fun visitValueNode(value: T, id: String): CodeBlock

    /**
     * Visit an add node (node that adds two input values).
     * @param left The left input node
     * @param right The right input node
     * @param id The ID of the node
     * @return The code block representing the compute node
     */
    fun visitAddNode(left: CodeBlock, right: CodeBlock, id: String): CodeBlock

    /**
     * Visit a multiply node (node that multiplies two input values).
     * @param left The left input node
     * @param right The right input node
     * @param id The ID of the node
     * @return The code block representing the compute node
     */
    fun visitMultiplyNode(left: CodeBlock, right: CodeBlock, id: String): CodeBlock
}

/**
 * Implementation of the ComputeNodeVisitor interface for generating code blocks.
 */
class CodeGeneratingVisitor : ComputeNodeVisitor<Double> {
    // Counter for generating unique variable names
    private var nodeCounter = 0

    override fun visitValueNode(value: Double, id: String): CodeBlock {
        val varName = generateNodeName("value")
        return CodeBlock.builder()
            .addStatement(
                "val $varName = %T($value).withId(%S)",
                ClassName("org.mikrograd.core", "ValueNode"),
                id
            )
            .build()
    }

    override fun visitAddNode(left: CodeBlock, right: CodeBlock, id: String): CodeBlock {
        val leftVarName = extractLastVarName(left)
        val rightVarName = extractLastVarName(right)
        val varName = generateNodeName("add")

        return CodeBlock.builder()
            .add(left)
            .add(right)
            .addStatement(
                "val $varName = %T<%T> { a, b -> a + b }.withId(%S)",
                ClassName("org.mikrograd.core", "AddNode"),
                ClassName("kotlin", "Double"),
                id
            )
            .addStatement("$varName.inputs.add($leftVarName)")
            .addStatement("$varName.inputs.add($rightVarName)")
            .build()
    }

    override fun visitMultiplyNode(left: CodeBlock, right: CodeBlock, id: String): CodeBlock {
        val leftVarName = extractLastVarName(left)
        val rightVarName = extractLastVarName(right)
        val varName = generateNodeName("multiply")

        return CodeBlock.builder()
            .add(left)
            .add(right)
            .addStatement(
                "val $varName = %T<%T> { a, b -> a * b }.withId(%S)",
                ClassName("org.mikrograd.core", "MultiplyNode"),
                ClassName("kotlin", "Double"),
                id
            )
            .addStatement("$varName.inputs.add($leftVarName)")
            .addStatement("$varName.inputs.add($rightVarName)")
            .build()
    }

    /**
     * Generate a unique variable name for a node.
     * @param prefix The prefix for the variable name
     * @return The generated variable name
     */
    private fun generateNodeName(prefix: String): String {
        return "${prefix}${nodeCounter++}"
    }

    /**
     * Extract the variable name from the last statement in a code block.
     * This is a simplistic implementation that assumes the last statement
     * in the code block is a variable declaration.
     * @param codeBlock The code block to extract from
     * @return The variable name
     */
    private fun extractLastVarName(codeBlock: CodeBlock): String {
        // Find the last variable declaration in the code block
        val statements = codeBlock.toString().trim().split("\n")
        for (i in statements.indices.reversed()) {
            val statement = statements[i]
            val match = Regex("val (\\w+)").find(statement)
            if (match != null) {
                return match.groupValues[1]
            }
        }

        // If no variable declaration is found, return a default name
        return "resultNode"
    }
}
