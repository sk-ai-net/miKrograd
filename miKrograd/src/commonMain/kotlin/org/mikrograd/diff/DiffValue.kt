package org.mikrograd.diff

// differentiable value
interface DiffValue<T> {
    val value: T
    val derivative: T
    val children: List<DiffValue<T>>
}