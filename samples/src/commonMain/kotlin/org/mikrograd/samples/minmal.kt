package org.mikrograd.samples

/*
import org.mikrograd.diff.MLP
import org.mikrograd.diff.Value
import org.mikrograd.utils.drawDot

fun loss(X: Array<DoubleArray>, y: DoubleArray, model: MLP): Pair<Value, Double> {
    val inputs: List<List<Value>> = X.mapIndexed { index, xrow -> xrow.map { Value(it, label = "in$index") } }
    val scores: List<Value> = inputs.map { input -> model(input).first() }

    // mean square error
    val values: List<Value> =
        y.zip(scores) { actual: Double, predicted: Value -> (actual - predicted).pow(2.0) }
    val mse = values.fold(Value(0.0)) { acc, value -> acc + value }

    return Pair(mse, 0.0)
}

fun main() {

    val c = Value(3.0, label = "a") + Value(2.0, label = "b")
    val cGr = drawDot(c)
    cGr.toFile("a+b.dot")

    val d = Value(3.0, label = "a") * Value(2.0, label = "b")
    val dGr = drawDot(d)
    dGr.toFile("a*b.dot")


    val model = MLP(1, listOf(1, 1, 1)) //# 2-layer neural network
    val (X, y) = Pair<Array<DoubleArray>, DoubleArray>(
        arrayOf(doubleArrayOf(1.0)),
        doubleArrayOf(2.0)
    )

    val X_v: List<List<Value>> = X.map { xrow -> xrow.map { Value(it) } }
    val prediction = model.invoke(X_v[0])[0]

    val modelGr = drawDot(prediction)
    modelGr.toFile("model.dot")

    val (loss, _) = loss(X, y, model)

    val lossGr = drawDot(loss)
    lossGr.toFile("loss.dot")

    model.zeroGrad()
    loss.backward()

    val backGr = drawDot(loss, true)
    backGr.toFile("back.dot")



}

 */