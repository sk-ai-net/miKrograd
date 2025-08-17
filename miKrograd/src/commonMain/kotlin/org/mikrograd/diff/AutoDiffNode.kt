package org.mikrograd.diff

/**
 * Value interface is values holder interface and defines all operations for automatic differentiation.
 * This allows for more flexibility in implementation and clearer API contract.
 */
interface AutoDiffNode {
    /** The data value */
    var data: Double

    /** The label for this value */
    var label: String

    /** The operation that produced this value */
    val op: String

    /** The child nodes in the computational graph */
    val children: List<AutoDiffNode>

    /** Addition operation */
    operator fun plus(other: AutoDiffNode): AutoDiffNode
    operator fun plus(other: Int): AutoDiffNode
    operator fun plus(other: Double): AutoDiffNode

    /** Multiplication operation */
    operator fun times(other: AutoDiffNode): AutoDiffNode
    operator fun times(other: Int): AutoDiffNode
    operator fun times(other: Double): AutoDiffNode

    /** Subtraction operation */
    operator fun minus(other: AutoDiffNode): AutoDiffNode
    operator fun unaryMinus(): AutoDiffNode

    /** Division operation */
    operator fun div(other: AutoDiffNode): AutoDiffNode
    operator fun div(other: Int): AutoDiffNode
    operator fun div(other: Double): AutoDiffNode

    /** Power operation */
    infix fun pow(other: Double): AutoDiffNode

    /** Activation functions */
    fun relu(): AutoDiffNode
    fun tanh(): AutoDiffNode
    fun sigmoid(): AutoDiffNode

    /** Backward pass for gradient computation */
    fun backward() {
        // Default empty implementation
    }
}
