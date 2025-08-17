package org.mikrograd.diff

import kotlin.math.exp
import kotlin.math.pow

typealias Value = BackpropNode

/**
 * BackwardValue class that extends ForwardValue to add gradient functionality.
 * This class includes both forward and backward pass capabilities.
 */
class BackpropNode(
    data: Double,
    _children: List<BackpropNode> = listOf(),
    _op: String = "",
    label: String = ""
) : ForwardPassNode(data, _children.map { it as ForwardPassNode }, _op, label) {
    var grad: Double = 0.0
    private var _backward: () -> Unit = {}

    init {
        _backward = { }
    }

    /**
     * Convert a ForwardValue to a BackwardValue.
     * This is used when we need to add gradient information to a ForwardValue.
     */
    constructor(forwardPassNode: ForwardPassNode) : this(
        forwardPassNode.data,
        forwardPassNode.children.map {
            if (it is BackpropNode) it else BackpropNode(it as ForwardPassNode)
        },
        forwardPassNode.op,
        forwardPassNode.label
    )

    operator fun plus(other: BackpropNode): BackpropNode {
        val out = BackpropNode(data + other.data, listOf(this, other), "+", label = "")
        out._backward = {
            this.grad += out.grad
            other.grad += out.grad
        }
        return out
    }

    override operator fun plus(other: ForwardPassNode): BackpropNode {
        return if (other is BackpropNode) {
            this.plus(other)
        } else {
            // Convert ForwardValue to BackwardValue
            this.plus(BackpropNode(other))
        }
    }

    operator fun times(other: BackpropNode): BackpropNode {
        val out = BackpropNode(data * other.data, listOf(this, other), "*", label = "")
        out._backward = {
            this.grad += other.data * out.grad
            other.grad += this.data * out.grad
        }
        return out
    }

    override operator fun times(other: ForwardPassNode): BackpropNode {
        return if (other is BackpropNode) {
            this.times(other)
        } else {
            // Convert ForwardValue to BackwardValue
            this.times(BackpropNode(other))
        }
    }

    override infix fun pow(other: Double): BackpropNode {
        val out = BackpropNode(data.pow(other), listOf(this), "^$other")
        out._backward = {
            this.grad += (other * data.pow(other - 1)) * out.grad
        }
        return out
    }

    override fun relu(): BackpropNode {
        val out = BackpropNode(if (data < 0) 0.0 else data, listOf(this), "ReLU", label = "Relu(${this.label})")
        out._backward = {
            if (data > 0) {
                this.grad += out.grad
            }
        }
        return out
    }

    override fun tanh(): BackpropNode {
        val t = (exp(2 * data) - 1) / (exp(2 * data) + 1)
        val out = BackpropNode(t, listOf(this), "tanh", label = "tanh(${this.label})")
        out._backward = {
            this.grad += (1 - t * t) * out.grad
        }
        return out
    }

    override fun sigmoid(): BackpropNode {
        val out = BackpropNode(1.0 / (1.0 + exp(-data)), listOf(this), "sigmoid", label = "sigmoid(${this.label})")
        out._backward = {
            val s = 1.0 / (1.0 + exp(-data))
            this.grad += s * (1.0 - s) * out.grad
        }
        return out
    }

    override operator fun unaryMinus(): BackpropNode = this.times(BackpropNode(-1.0, _op = ""))

    operator fun minus(other: BackpropNode): BackpropNode = this + (-other)

    override operator fun minus(other: ForwardPassNode): BackpropNode {
        return if (other is BackpropNode) {
            this.minus(other)
        } else {
            this.minus(BackpropNode(other))
        }
    }

    override operator fun plus(other: Int): BackpropNode = this + BackpropNode(other.toDouble(), _op = "+")
    override operator fun plus(other: Double): BackpropNode = this + BackpropNode(other, _op = "+")

    override operator fun div(other: Int): BackpropNode = this * BackpropNode(other.toDouble()).pow(-1.0)
    override operator fun div(other: Double): BackpropNode = this * BackpropNode(other).pow(-1.0)

    operator fun div(other: BackpropNode): BackpropNode = this * other.pow(-1.0)

    override operator fun div(other: ForwardPassNode): BackpropNode {
        return if (other is BackpropNode) {
            this.div(other)
        } else {
            this.div(BackpropNode(other))
        }
    }

    override operator fun times(other: Int): BackpropNode = this.times(BackpropNode(other.toDouble(), _op = "*"))
    override operator fun times(other: Double): BackpropNode = this.times(BackpropNode(other, _op = "*"))

    /**
     * Compute gradients for the computational graph.
     */
    override fun backward() {
        val topo = mutableListOf<BackpropNode>()
        val visited = mutableSetOf<BackpropNode>()

        fun buildTopo(v: BackpropNode) {
            if (!visited.contains(v)) {
                visited.add(v)
                v.children.filterIsInstance<BackpropNode>().forEach { buildTopo(it) }
                topo.add(v)
            }
        }

        buildTopo(this)

        grad = 1.0
        val reversed = topo.asReversed()
        reversed.forEach { it._backward() }
    }

    override fun toString(): String = "Backprop(data=$data, grad=$grad, op=$op, label='$label')"
}

// Extension functions for Double and Int to seamlessly interact with BackwardValue instances
operator fun Int.plus(value: BackpropNode): BackpropNode = BackpropNode(this.toDouble(), _op = "").plus(value)
operator fun Double.plus(value: BackpropNode): BackpropNode = BackpropNode(this, _op = "").plus(value)

operator fun Int.times(value: BackpropNode): BackpropNode = BackpropNode(this.toDouble(), _op = "").times(value)
operator fun Double.times(value: BackpropNode): BackpropNode = BackpropNode(this, _op = "").times(value)

operator fun Int.minus(value: BackpropNode): BackpropNode = BackpropNode(this.toDouble(), _op = "").minus(value)
operator fun Double.minus(value: BackpropNode): BackpropNode = BackpropNode(this, _op = "").minus(value)

operator fun Int.div(value: BackpropNode): BackpropNode = BackpropNode(this.toDouble(), _op = "").div(value)
operator fun Double.div(value: BackpropNode): BackpropNode = BackpropNode(this, _op = "").div(value)
