package org.mikrograd.diff

import kotlin.math.exp
import kotlin.math.pow

class Value(
    var data: Double,
    val _children: List<Value> = listOf(),
    private val _op: String = "",
    var label: String = ""
) {
    var grad: Double = 0.0
    private var _backward: () -> Unit = {}

    val children: List<Value>
        get() = _children

    val op: String
        get() = _op

    init {
        _backward = { }
    }

    operator fun plus(other: Value): Value {
        val out = Value(data + other.data, listOf(this, other), "+", label = "")
        out._backward = {
            this.grad += out.grad
            other.grad += out.grad
        }
        return out
    }

    operator fun times(other: Value): Value {
        val out = Value(data * other.data, listOf(this, other), "*", label = "")
        out._backward = {
            this.grad += other.data * out.grad
            other.grad += this.data * out.grad
        }
        return out
    }

    infix fun pow(other: Double): Value {
        val out = Value(data.pow(other), listOf(this), "^$other")
        out._backward = {
            this.grad += (other * data.pow(other - 1)) * out.grad
        }
        return out
    }

    fun relu(): Value {
        val out = Value(if (data < 0) 0.0 else data, listOf(this), "ReLU", label = "Relu(${this.label})")
        out._backward = {
            if (data > 0) {
                this.grad += out.grad
            } else {
                this.grad = 0.0
            }
        }
        return out
    }

    fun tanh(): Value {
        val t = (exp(2 * data) - 1) / (exp(2 * data) + 1)
        val out = Value(t, listOf(this), "tanh", label = "tanh(${this.label})")
        out._backward = {
            this.grad += (1 - t * t) * out.grad
        }
        return out
    }

    fun sigmoid(): Value {
        val out = Value(1.0 / (1.0 + exp(-data)), listOf(this), "sigmoid", label = "sigmoid(${this.label})")
        out._backward = {
            val s = 1.0 / (1.0 + exp(-data))
            this.grad = s * (1.0 - s)
        }
        return out
    }


    fun backward() {
        val topo = mutableListOf<Value>()
        val visited = mutableSetOf<Value>()

        fun buildTopo(v: Value) {
            if (!visited.contains(v)) {
                visited.add(v)
                v._children.forEach { buildTopo(it) }
                topo.add(v)
            }
        }

        buildTopo(this)

        grad = 1.0
        val reversed = topo.asReversed()
        reversed.forEach { it._backward() }
    }

    operator fun unaryMinus() = this.times(Value(-1.0, _op = ""))

    operator fun minus(other: Value) = this + (-other)
    operator fun plus(other: Int) = this + Value(other.toDouble(), _op = "+")

    operator fun div(other: Int) = this * Value(other.toDouble()).pow(-1.0)
    operator fun div(other: Double) = this * Value(other).pow(-1.0)
    operator fun div(other: Value) = this * other.pow(-1.0)

    operator fun times(other: Int) = this.times(Value(other.toDouble(), _op = "*"))

    operator fun times(other: Double) = this.times(Value(other, _op = "*"))


    override fun toString(): String = "Value(data=$data, grad=$grad, op=$_op, label='$label')"
}

// Extension functions for Double to seamlessly interact with Value instances
operator fun Int.plus(value: Value): Value = Value(this.toDouble(), _op = "") + value
operator fun Double.plus(value: Value): Value = Value(this, _op = "") + value

operator fun Int.times(value: Value): Value = Value(this.toDouble(), _op = "") * value
operator fun Double.times(value: Value): Value = Value(this, _op = "") * value

operator fun Int.minus(value: Value): Value = Value(this.toDouble(), _op = "") - value
operator fun Double.minus(value: Value): Value = Value(this, _op = "") - value


operator fun Int.div(value: Value): Value = Value(this.toDouble(), _op = "") / value
operator fun Double.div(value: Value): Value = Value(this, _op = "") / value
