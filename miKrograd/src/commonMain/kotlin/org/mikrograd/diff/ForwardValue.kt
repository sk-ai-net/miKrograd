package org.mikrograd.diff

import kotlin.math.exp
import kotlin.math.pow

/**
 * ForwardValue class that only stores data needed for forward pass operations.
 * This class doesn't include gradient information to save memory when only forward pass is needed.
 */
open class ForwardValue(
    override var data: Double,
    val _children: List<ForwardValue> = listOf(),
    private val _op: String = "",
    override var label: String = ""
) : ValueInterface {
    val forwardChildren: List<ForwardValue>
        get() = _children

    @Suppress("UNCHECKED_CAST")
    override val children: List<ValueInterface>
        get() = _children as List<ValueInterface>

    override val op: String
        get() = _op

    // ValueInterface implementation
    override operator fun plus(other: ValueInterface): ValueInterface {
        return if (other is ForwardValue) {
            plus(other)
        } else {
            // This should not happen in normal usage, but we need to handle it for the interface
            ForwardValue(data + other.data, listOf(this), "+", label = "")
        }
    }

    override operator fun times(other: ValueInterface): ValueInterface {
        return if (other is ForwardValue) {
            times(other)
        } else {
            // This should not happen in normal usage, but we need to handle it for the interface
            ForwardValue(data * other.data, listOf(this), "*", label = "")
        }
    }

    override operator fun minus(other: ValueInterface): ValueInterface {
        return if (other is ForwardValue) {
            minus(other)
        } else {
            // This should not happen in normal usage, but we need to handle it for the interface
            ForwardValue(data - other.data, listOf(this), "-", label = "")
        }
    }

    override operator fun div(other: ValueInterface): ValueInterface {
        return if (other is ForwardValue) {
            div(other)
        } else {
            // This should not happen in normal usage, but we need to handle it for the interface
            ForwardValue(data / other.data, listOf(this), "/", label = "")
        }
    }

    open operator fun plus(other: ForwardValue): ForwardValue {
        // If the other value is a BackwardValue, let it handle the operation
        // This ensures that operations between ForwardValue and BackwardValue return BackwardValue
        if (other is BackwardValue) {
            return other.plus(this)
        }
        return ForwardValue(data + other.data, listOf(this, other), "+", label = "")
    }

    open operator fun times(other: ForwardValue): ForwardValue {
        // If the other value is a BackwardValue, let it handle the operation
        if (other is BackwardValue) {
            return other.times(this)
        }
        return ForwardValue(data * other.data, listOf(this, other), "*", label = "")
    }

    override infix fun pow(other: Double): ValueInterface {
        return ForwardValue(data.pow(other), listOf(this), "^$other")
    }

    override fun relu(): ValueInterface {
        return ForwardValue(if (data < 0) 0.0 else data, listOf(this), "ReLU", label = "Relu(${this.label})")
    }

    override fun tanh(): ValueInterface {
        val t = (exp(2 * data) - 1) / (exp(2 * data) + 1)
        return ForwardValue(t, listOf(this), "tanh", label = "tanh(${this.label})")
    }

    override fun sigmoid(): ValueInterface {
        return ForwardValue(1.0 / (1.0 + exp(-data)), listOf(this), "sigmoid", label = "sigmoid(${this.label})")
    }

    override operator fun unaryMinus(): ValueInterface = this.times(ForwardValue(-1.0, _op = ""))

    open operator fun minus(other: ForwardValue): ValueInterface {
        // If the other value is a BackwardValue, let it handle the operation
        if (other is BackwardValue) {
            return other.unaryMinus().plus(this)
        }
        return this + (-other)
    }

    override operator fun plus(other: Int): ValueInterface = this + ForwardValue(other.toDouble(), _op = "+")
    override operator fun plus(other: Double): ValueInterface = this + ForwardValue(other, _op = "+")

    override operator fun div(other: Int): ValueInterface = this * ForwardValue(other.toDouble()).pow(-1.0)
    override operator fun div(other: Double): ValueInterface = this * ForwardValue(other).pow(-1.0)
    open operator fun div(other: ForwardValue): ValueInterface {
        // If the other value is a BackwardValue, let it handle the operation
        if (other is BackwardValue) {
            return other.pow(-1.0).times(this)
        }
        return this * other.pow(-1.0)
    }

    override operator fun times(other: Int): ValueInterface = this.times(ForwardValue(other.toDouble(), _op = "*"))
    override operator fun times(other: Double): ValueInterface = this.times(ForwardValue(other, _op = "*"))

    override fun toString(): String = "ForwardValue(data=$data, op=$_op, label='$label')"

    /**
     * Convert this ForwardValue to a BackwardValue when gradient information is needed.
     * This will be implemented in a separate class.
     */
}

// Extension functions for Double and Int to seamlessly interact with ForwardValue instances
operator fun Int.plus(value: ForwardValue): ValueInterface = ForwardValue(this.toDouble(), _op = "") + value
operator fun Double.plus(value: ForwardValue): ValueInterface = ForwardValue(this, _op = "") + value

operator fun Int.times(value: ForwardValue): ValueInterface = ForwardValue(this.toDouble(), _op = "") * value
operator fun Double.times(value: ForwardValue): ValueInterface = ForwardValue(this, _op = "") * value

operator fun Int.minus(value: ForwardValue): ValueInterface = ForwardValue(this.toDouble(), _op = "") - value
operator fun Double.minus(value: ForwardValue): ValueInterface = ForwardValue(this, _op = "") - value

operator fun Int.div(value: ForwardValue): ValueInterface = ForwardValue(this.toDouble(), _op = "") / value
operator fun Double.div(value: ForwardValue): ValueInterface = ForwardValue(this, _op = "") / value
