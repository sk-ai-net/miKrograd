package org.mikrograd.diff

import kotlin.math.exp
import kotlin.math.pow

/**
 * BackwardValue class that extends ForwardValue to add gradient functionality.
 * This class includes both forward and backward pass capabilities.
 */
class BackwardValue(
    data: Double,
    _children: List<BackwardValue> = listOf(),
    _op: String = "",
    label: String = ""
) : ForwardValue(data, _children.map { it as ForwardValue }, _op, label) {
    var grad: Double = 0.0
    private var _backward: () -> Unit = {}

    init {
        _backward = { }
    }

    /**
     * Convert a ForwardValue to a BackwardValue.
     * This is used when we need to add gradient information to a ForwardValue.
     */
    constructor(forwardValue: ForwardValue) : this(
        forwardValue.data,
        forwardValue.children.map { 
            if (it is BackwardValue) it else BackwardValue(it as ForwardValue) 
        },
        forwardValue.op,
        forwardValue.label
    )

    operator fun plus(other: BackwardValue): BackwardValue {
        val out = BackwardValue(data + other.data, listOf(this, other), "+", label = "")
        out._backward = {
            this.grad += out.grad
            other.grad += out.grad
        }
        return out
    }

    override operator fun plus(other: ForwardValue): BackwardValue {
        return if (other is BackwardValue) {
            this.plus(other)
        } else {
            // Convert ForwardValue to BackwardValue
            this.plus(BackwardValue(other))
        }
    }

    operator fun times(other: BackwardValue): BackwardValue {
        val out = BackwardValue(data * other.data, listOf(this, other), "*", label = "")
        out._backward = {
            this.grad += other.data * out.grad
            other.grad += this.data * out.grad
        }
        return out
    }

    override operator fun times(other: ForwardValue): BackwardValue {
        return if (other is BackwardValue) {
            this.times(other)
        } else {
            // Convert ForwardValue to BackwardValue
            this.times(BackwardValue(other))
        }
    }

    override infix fun pow(other: Double): BackwardValue {
        val out = BackwardValue(data.pow(other), listOf(this), "^$other")
        out._backward = {
            this.grad += (other * data.pow(other - 1)) * out.grad
        }
        return out
    }

    override fun relu(): BackwardValue {
        val out = BackwardValue(if (data < 0) 0.0 else data, listOf(this), "ReLU", label = "Relu(${this.label})")
        out._backward = {
            if (data > 0) {
                this.grad += out.grad
            }
        }
        return out
    }

    override fun tanh(): BackwardValue {
        val t = (exp(2 * data) - 1) / (exp(2 * data) + 1)
        val out = BackwardValue(t, listOf(this), "tanh", label = "tanh(${this.label})")
        out._backward = {
            this.grad += (1 - t * t) * out.grad
        }
        return out
    }

    override fun sigmoid(): BackwardValue {
        val out = BackwardValue(1.0 / (1.0 + exp(-data)), listOf(this), "sigmoid", label = "sigmoid(${this.label})")
        out._backward = {
            val s = 1.0 / (1.0 + exp(-data))
            this.grad += s * (1.0 - s) * out.grad
        }
        return out
    }

    override operator fun unaryMinus(): BackwardValue = this.times(BackwardValue(-1.0, _op = ""))

    operator fun minus(other: BackwardValue): BackwardValue = this + (-other)

    override operator fun minus(other: ForwardValue): BackwardValue {
        return if (other is BackwardValue) {
            this.minus(other)
        } else {
            this.minus(BackwardValue(other))
        }
    }

    override operator fun plus(other: Int): BackwardValue = this + BackwardValue(other.toDouble(), _op = "+")
    override operator fun plus(other: Double): BackwardValue = this + BackwardValue(other, _op = "+")

    override operator fun div(other: Int): BackwardValue = this * BackwardValue(other.toDouble()).pow(-1.0)
    override operator fun div(other: Double): BackwardValue = this * BackwardValue(other).pow(-1.0)

    operator fun div(other: BackwardValue): BackwardValue = this * other.pow(-1.0)

    override operator fun div(other: ForwardValue): BackwardValue {
        return if (other is BackwardValue) {
            this.div(other)
        } else {
            this.div(BackwardValue(other))
        }
    }

    override operator fun times(other: Int): BackwardValue = this.times(BackwardValue(other.toDouble(), _op = "*"))
    override operator fun times(other: Double): BackwardValue = this.times(BackwardValue(other, _op = "*"))

    /**
     * Compute gradients for the computational graph.
     */
    override fun backward() {
        val topo = mutableListOf<BackwardValue>()
        val visited = mutableSetOf<BackwardValue>()

        fun buildTopo(v: BackwardValue) {
            if (!visited.contains(v)) {
                visited.add(v)
                v.children.filterIsInstance<BackwardValue>().forEach { buildTopo(it) }
                topo.add(v)
            }
        }

        buildTopo(this)

        grad = 1.0
        val reversed = topo.asReversed()
        reversed.forEach { it._backward() }
    }

    override fun toString(): String = "BackwardValue(data=$data, grad=$grad, op=$op, label='$label')"
}

// Extension functions for Double and Int to seamlessly interact with BackwardValue instances
operator fun Int.plus(value: BackwardValue): BackwardValue = BackwardValue(this.toDouble(), _op = "").plus(value)
operator fun Double.plus(value: BackwardValue): BackwardValue = BackwardValue(this, _op = "").plus(value)

operator fun Int.times(value: BackwardValue): BackwardValue = BackwardValue(this.toDouble(), _op = "").times(value)
operator fun Double.times(value: BackwardValue): BackwardValue = BackwardValue(this, _op = "").times(value)

operator fun Int.minus(value: BackwardValue): BackwardValue = BackwardValue(this.toDouble(), _op = "").minus(value)
operator fun Double.minus(value: BackwardValue): BackwardValue = BackwardValue(this, _op = "").minus(value)

operator fun Int.div(value: BackwardValue): BackwardValue = BackwardValue(this.toDouble(), _op = "").div(value)
operator fun Double.div(value: BackwardValue): BackwardValue = BackwardValue(this, _op = "").div(value)
