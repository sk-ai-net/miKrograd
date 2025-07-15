package org.mikrograd.diff

/**
 * ValueInterface that defines all operations for automatic differentiation.
 * This allows for more flexibility in implementation and clearer API contract.
 */
interface ValueInterface {
    /** The data value */
    var data: Double

    /** The label for this value */
    var label: String

    /** The operation that produced this value */
    val op: String

    /** The child nodes in the computational graph */
    val children: List<ValueInterface>

    /** Addition operation */
    operator fun plus(other: ValueInterface): ValueInterface
    operator fun plus(other: Int): ValueInterface
    operator fun plus(other: Double): ValueInterface

    /** Multiplication operation */
    operator fun times(other: ValueInterface): ValueInterface
    operator fun times(other: Int): ValueInterface
    operator fun times(other: Double): ValueInterface

    /** Subtraction operation */
    operator fun minus(other: ValueInterface): ValueInterface
    operator fun unaryMinus(): ValueInterface

    /** Division operation */
    operator fun div(other: ValueInterface): ValueInterface
    operator fun div(other: Int): ValueInterface
    operator fun div(other: Double): ValueInterface

    /** Power operation */
    infix fun pow(other: Double): ValueInterface

    /** Activation functions */
    fun relu(): ValueInterface
    fun tanh(): ValueInterface
    fun sigmoid(): ValueInterface

    /** Backward pass for gradient computation */
    fun backward() {
        // Default empty implementation
    }
}


/**
 * Value is now a typealias to ValueInterface for better abstraction.
 * This allows for more flexibility in implementation.
 */
typealias Value = ValueInterface

/*
// Extension functions for Double and Int to seamlessly interact with ValueInterface instances
operator fun Int.plus(value: ValueInterface): ValueInterface = ValueFactory.create(this.toDouble()) + value
operator fun Double.plus(value: ValueInterface): ValueInterface = ValueFactory.create(this) + value

operator fun Int.times(value: ValueInterface): ValueInterface = ValueFactory.create(this.toDouble()) * value
operator fun Double.times(value: ValueInterface): ValueInterface = ValueFactory.create(this) * value

operator fun Int.minus(value: ValueInterface): ValueInterface = ValueFactory.create(this.toDouble()) - value
operator fun Double.minus(value: ValueInterface): ValueInterface = ValueFactory.create(this) - value

operator fun Int.div(value: ValueInterface): ValueInterface = ValueFactory.create(this.toDouble()) / value
operator fun Double.div(value: ValueInterface): ValueInterface = ValueFactory.create(this) / value
*/