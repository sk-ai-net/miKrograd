package com.example

class Value(
    var data: Double,
    var label: String = ""
)

@Label
val w1 = Value(0.15)

@Label
val w2 = Value(0.3)

fun main() {
    setAllLabels()
    println(w1.label) // Should print "w1"
    println(w2.label) // Should print "w2"
}

