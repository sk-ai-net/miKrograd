package org.mikrograd.core

/**
 * Abstract base class for all nodes in a computation graph.
 *
 * A computation graph is a directed graph where nodes represent operations and edges
 * represent data dependencies between operations. Each node can have multiple inputs
 * and produces a single output of type [T].
 *
 * @param T The type of data processed by this computation node.
 */
abstract class ComputeNode<T> {
    /**
     * Returns a string representation of this node, including its ID and class name.
     *
     * @return A string representation of this node.
     */
    override fun toString(): String = "${this::class.simpleName}(id=$id)"
    /**
     * Unique identifier for this computation node.
     *
     * Each node in the computation graph has a unique ID that can be used for
     * debugging, visualization, and tracking nodes in complex graphs.
     */
    var id: String = generateId()
        private set

    /**
     * Sets a custom ID for this node.
     *
     * This method can be used to assign a meaningful name to a node for debugging
     * or visualization purposes.
     *
     * @param customId The custom ID to assign to this node.
     * @return This node, to allow for method chaining.
     */
    fun withId(customId: String): ComputeNode<T> {
        id = customId
        return this
    }

    companion object {
        /**
         * Counter used to generate unique IDs for nodes.
         */
        private var idCounter = 0

        /**
         * Generates a unique ID for a node.
         *
         * @return A unique ID string.
         */
        private fun generateId(): String {
            return "node_${idCounter++}"
        }
    }

    /**
     * The list of input nodes for this computation node.
     *
     * The inputs represent the dependencies of this node. When this node is evaluated,
     * all its inputs are evaluated first, and their results are used to compute the
     * result of this node.
     */
    val inputs = mutableListOf<ComputeNode<T>>()

    /**
     * Evaluates this node and returns the result.
     *
     * The evaluation recursively evaluates all input nodes first, then computes the
     * result of this node based on the results of its inputs.
     *
     * @return The result of the computation.
     */
    abstract fun evaluate(): T
}

/**
 * A leaf node in the computation graph that holds a constant value.
 *
 * @param T The type of the value.
 * @property value The constant value held by this node.
 */
class ValueNode<T>(private val value: T) : ComputeNode<T>() {
    /**
     * Returns the constant value held by this node.
     *
     * @return The constant value.
     */
    override fun evaluate(): T = value
}

/**
 * A node that adds two input values.
 *
 * @param T The type of the values to add.
 * @property add A function that defines how to add two values of type [T].
 */
open class AddNode<T>(private val add: (T, T) -> T) : ComputeNode<T>() {
    /**
     * Evaluates the two input nodes and adds their results.
     *
     * @return The result of adding the two input values.
     */
    override fun evaluate(): T = add(inputs[0].evaluate(), inputs[1].evaluate())
}

/**
 * A node that multiplies two input values.
 *
 * @param T The type of the values to multiply.
 * @property multiply A function that defines how to multiply two values of type [T].
 */
class MultiplyNode<T>(private val multiply: (T, T) -> T) : ComputeNode<T>() {
    /**
     * Evaluates the two input nodes and multiplies their results.
     *
     * @return The result of multiplying the two input values.
     */
    override fun evaluate(): T = multiply(inputs[0].evaluate(), inputs[1].evaluate())
}

/**
 * A node that applies an activation function to its input.
 *
 * Activation functions are commonly used in neural networks to introduce
 * non-linearity into the model.
 *
 * @param T The type of the value to activate.
 * @property differentiableFunction The activation function to apply.
 * @property name The name of the activation function, used for debugging.
 */
class DifferentiableFunctionNode<T>(
    private val differentiableFunction: (T) -> T,
    private val name: String
) : ComputeNode<T>() {
    /**
     * Evaluates the input node and applies the activation function to its result.
     *
     * @return The result of applying the activation function to the input value.
     */
    override fun evaluate() = differentiableFunction(inputs[0].evaluate())

    /**
     * Returns a string representation of this node, including its ID and the name
     * of the differentiable function.
     *
     * @return A string representation of this node.
     */
    override fun toString() = "$name(id=$id)"
}
