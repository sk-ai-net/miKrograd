package org.mikrograd.diff

import kotlin.math.exp
import kotlin.math.pow

/**
 * ForwardValue class that only stores data needed for forward pass operations.
 * This class doesn't include gradient information to save memory when only forward pass is needed.
 */
open class ForwardPassNode(
    override var data: Double,
    val _children: List<ForwardPassNode> = listOf(),
    private val _op: String = "",
    override var label: String = ""
) : AutoDiffNode {
    val forwardChildren: List<ForwardPassNode>
        get() = _children

    @Suppress("UNCHECKED_CAST")
    override val children: List<AutoDiffNode>
        get() = _children as List<AutoDiffNode>

    override val op: String
        get() = _op

    // Value implementation
    override operator fun plus(other: AutoDiffNode): AutoDiffNode {
        return if (other is ForwardPassNode) {
            plus(other)
        } else {
            // This should not happen in normal usage, but we need to handle it for the interface
            ForwardPassNode(data + other.data, listOf(this), "+", label = "")
        }
    }

    override operator fun times(other: AutoDiffNode): AutoDiffNode {
        return if (other is ForwardPassNode) {
            times(other)
        } else {
            // This should not happen in normal usage, but we need to handle it for the interface
            ForwardPassNode(data * other.data, listOf(this), "*", label = "")
        }
    }

    override operator fun minus(other: AutoDiffNode): AutoDiffNode {
        return if (other is ForwardPassNode) {
            minus(other)
        } else {
            // This should not happen in normal usage, but we need to handle it for the interface
            ForwardPassNode(data - other.data, listOf(this), "-", label = "")
        }
    }

    override operator fun div(other: AutoDiffNode): AutoDiffNode {
        return if (other is ForwardPassNode) {
            div(other)
        } else {
            // This should not happen in normal usage, but we need to handle it for the interface
            ForwardPassNode(data / other.data, listOf(this), "/", label = "")
        }
    }

    open operator fun plus(other: ForwardPassNode): ForwardPassNode {
        // If the other value is a BackwardValue, let it handle the operation
        // This ensures that operations between ForwardValue and BackwardValue return BackwardValue
        if (other is BackpropNode) {
            return other.plus(this)
        }
        return ForwardPassNode(data + other.data, listOf(this, other), "+", label = "")
    }

    open operator fun times(other: ForwardPassNode): ForwardPassNode {
        // If the other value is a BackwardValue, let it handle the operation
        if (other is BackpropNode) {
            return other.times(this)
        }
        return ForwardPassNode(data * other.data, listOf(this, other), "*", label = "")
    }

    override infix fun pow(other: Double): AutoDiffNode {
        return ForwardPassNode(data.pow(other), listOf(this), "^$other")
    }

    override fun relu(): AutoDiffNode {
        return ForwardPassNode(if (data < 0) 0.0 else data, listOf(this), "ReLU", label = "Relu(${this.label})")
    }

    override fun tanh(): AutoDiffNode {
        val t = (exp(2 * data) - 1) / (exp(2 * data) + 1)
        return ForwardPassNode(t, listOf(this), "tanh", label = "tanh(${this.label})")
    }

    override fun sigmoid(): AutoDiffNode {
        return ForwardPassNode(1.0 / (1.0 + exp(-data)), listOf(this), "sigmoid", label = "sigmoid(${this.label})")
    }

    override operator fun unaryMinus(): AutoDiffNode = this.times(ForwardPassNode(-1.0, _op = ""))

    open operator fun minus(other: ForwardPassNode): AutoDiffNode {
        // If the other value is a BackwardValue, let it handle the operation
        if (other is BackpropNode) {
            return other.unaryMinus().plus(this)
        }
        return this + (-other)
    }

    override operator fun plus(other: Int): AutoDiffNode = this + ForwardPassNode(other.toDouble(), _op = "+")
    override operator fun plus(other: Double): AutoDiffNode = this + ForwardPassNode(other, _op = "+")

    override operator fun div(other: Int): AutoDiffNode = this * ForwardPassNode(other.toDouble()).pow(-1.0)
    override operator fun div(other: Double): AutoDiffNode = this * ForwardPassNode(other).pow(-1.0)
    open operator fun div(other: ForwardPassNode): AutoDiffNode {
        // If the other value is a BackwardValue, let it handle the operation
        if (other is BackpropNode) {
            return other.pow(-1.0).times(this)
        }
        return this * other.pow(-1.0)
    }

    override operator fun times(other: Int): AutoDiffNode = this.times(ForwardPassNode(other.toDouble(), _op = "*"))
    override operator fun times(other: Double): AutoDiffNode = this.times(ForwardPassNode(other, _op = "*"))

    override fun toString(): String = "ForwardValue(data=$data, op=$_op, label='$label')"

    /**
     * Convert this ForwardValue to a BackwardValue when gradient information is needed.
     * This will be implemented in a separate class.
     */
}

// Extension functions for Double and Int to seamlessly interact with ForwardValue instances
operator fun Int.plus(value: ForwardPassNode): AutoDiffNode = ForwardPassNode(this.toDouble(), _op = "") + value
operator fun Double.plus(value: ForwardPassNode): AutoDiffNode = ForwardPassNode(this, _op = "") + value

operator fun Int.times(value: ForwardPassNode): AutoDiffNode = ForwardPassNode(this.toDouble(), _op = "") * value
operator fun Double.times(value: ForwardPassNode): AutoDiffNode = ForwardPassNode(this, _op = "") * value

operator fun Int.minus(value: ForwardPassNode): AutoDiffNode = ForwardPassNode(this.toDouble(), _op = "") - value
operator fun Double.minus(value: ForwardPassNode): AutoDiffNode = ForwardPassNode(this, _op = "") - value

operator fun Int.div(value: ForwardPassNode): AutoDiffNode = ForwardPassNode(this.toDouble(), _op = "") / value
operator fun Double.div(value: ForwardPassNode): AutoDiffNode = ForwardPassNode(this, _op = "") / value
