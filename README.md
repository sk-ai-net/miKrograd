# miKrograd

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/sk.ainet.mikrograd/miKrograd.svg)](https://central.sonatype.com/artifact/sk.ainet.mikrograd/miKrograd)

A Kotlin multiplatform automatic differentiation library inspired by [micrograd](https://github.com/karpathy/micrograd) by Andrej Karpathy, featuring compile-time optimization through KSP (Kotlin Symbol Processing).

### Core Features
- **KSP Code Generation**: Compile-time optimization for mathematical expressions
- **Multi-Module Architecture**: Separate modules for core library, annotations, processor, and samples
- **Visualization Support**: Graphviz integration for computational graph visualization

### Modules
- **miKrograd**: Core automatic differentiation engine
- **miKrograd-annotations**: KSP annotations for compile-time code generation
- **miKrograd-processor**: KSP processor for optimized code generation
- **samples**: Example implementations and demonstrations

## Quick Start


### KSP Code Generation


```kotlin
import org.mikrograd.diff.BackpropNode
import org.mikrograd.diff.div
import org.mikrograd.diff.plus

typealias Value = BackpropNode

val a = Value(-4.0)
val b = Value(2.0)
var c = a + b
var d = a * b + b.pow(3.0)
c += c + 1
c += 1.0 + c + (-a)
d += d * 2 + (b + a).relu()
d += d * 3.0 + (b - a).relu()
val e = c - d
val f = e.pow(2.0)
var g = f / 2
g += 10.0 / f
println("$g")  // prints 24.7041, the outcome of this forward pass
g.backward()
println("${a.grad}") // prints 138.8338, i.e. the numerical value of dg/da
println("${b.grad}") // prints 645.5773, i.e. the numerical value of dg/db```

```

### Compile-Time Optimized Inference and Training

```kotlin
// Compile-time optimized inference
@Mikrograd(mode = ComputationMode.INFERENCE)
@Mikrograd
fun optimizedInference() {
    3.0 * 4.0 + (7.0 + 3.0)
}

// Compile-time optimized training with gradients  
@Mikrograd(mode = ComputationMode.TRAINING)
fun optimizedTraining() {
    3.0 * 4.0 + (7.0 + 3.0)
}
```

### Visualization Support

The library includes Graphviz integration for visualizing computational graphs:

```kotlin
fun main() {
    val nn = Neuron(2)
    val x = listOf(Value(1.0), Value(-2.0))
    val y = nn(x)
    drawDot(y).toFile("neuron.dot")
}
```

![2d neuron](neuron.svg)

## Installation

Add the following to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("org.mikrograd:mikrograd:latest.version")
    ksp("org.mikrograd:mikrograd-processor:latest.version")
}
```

## Building

```bash
./gradlew build
```

## Documentation

- **Architecture Documentation**: See [docs/README.md](docs/README.md) for comprehensive arc42 architecture documentation
- **Core Module**: See [miKrograd/README.md](miKrograd/README.md) for detailed API documentation
- **KSP Processor**: See [miKrograd-processor/README.md](miKrograd-processor/README.md) for code generation details
- **Examples**: Check the [samples](samples/) directory for working examples

## Project Structure

```
miKrograd/
├── miKrograd/              # Core automatic differentiation library
├── miKrograd-annotations/  # KSP annotations
├── miKrograd-processor/    # KSP code generation processor  
├── samples/               # Example implementations
└── docs/                  # Architecture documentation (arc42)
```

## Key Advantages

1. **Compile-Time Optimization**: KSP generates optimized code at compile time
2. **Type Safe**: Full Kotlin type safety with multiplatform support
3. **Extensible**: Well-documented architecture for easy extension

### License

MIT
