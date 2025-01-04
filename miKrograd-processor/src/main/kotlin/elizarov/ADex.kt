package elizarov

import kotlin.math.*

// Extensions for differentiation of various basic mathematical functions

// x ^ 2
fun AD.sqr(x: D): D = derive(D(x.x * x.x)) { z ->
    x.d += z.d * 2 * x.x
}

// x ^ 1/2
fun AD.sqrt(x: D): D = derive(D(sqrt(x.x))) { z ->
    x.d += z.d * 0.5 / z.x
}

// x ^ y (const)
fun AD.pow(x: D, y: Double): D = derive(D(x.x.pow(y))) { z ->
    x.d += z.d * y * x.x.pow(y - 1)
}

fun AD.pow(x: D, y: Int): D = pow(x, y.toDouble())

// exp(x)
fun AD.exp(x: D): D = derive(D(exp(x.x))) { z ->
    x.d += z.d * z.x
}

// ln(x)
fun AD.ln(x: D): D = derive(D(ln(x.x))) { z ->
    x.d += z.d / x.x
}

// x ^ y (any)
fun AD.pow(x: D, y: D): D = exp(y * ln(x))

// sin(x)
fun AD.sin(x: D): D = derive(D(sin(x.x))) { z ->
    x.d += z.d * cos(x.x)
}

// cos(x)
fun AD.cos(x: D): D = derive(D(cos(x.x))) { z ->
    x.d -= z.d * sin(x.x)
}