package org.mikrograd.samples
/*
import org.mikrograd.diff.MLP
import kotlin.random.Random
import org.mikrograd.diff.Value


class MLPClustering(private val data: Pair<Array<DoubleArray>, IntArray>, val model: MLP ) {
    private val X: Array<DoubleArray> = data.first
    private val y: IntArray = data.second

      fun loss(batchSize: Int? = null): Pair<Value, Double> {
        val (Xb, yb) = if (batchSize == null) {
            Pair(X.toList(), y.toList())
        } else {
            val ri = List(batchSize) { Random.nextInt(X.size) }
            Pair(ri.map { X[it] }, ri.map { y[it] })
        }
        val xc: List<DoubleArray> = Xb
        val inputs: List<List<Value>> = Xb.map { xrow -> xrow.map { Value(it) } }

        val scores: List<Value> = inputs.flatMap { input ->  model.invoke (input) }

        //losses = [(1 + -yi*scorei).relu() for yi, scorei in zip(yb, scores)]


        val losses: List<Value> = yb.zip(scores).map { (yi, scorei) -> Value(1 + -yi * scorei.data).relu() }
        val lossesSum: Value = losses.fold(Value(0.0)) { a, i -> a + i }

        val dataLoss: Value = lossesSum / losses.size

        val alpha = 1e-4
        val regLoss: Value = alpha  * (model.parameters().reduce { a, i -> a * i })
        //val reg_loss = alpha * sum((p*p for p in model.parameters()))
        val totalLoss = dataLoss + regLoss

        val accuracy = yb.zip(scores).count { (yi, scorei) -> (yi > 0) == (scorei.data > 0) }.toDouble() / yb.size

        return Pair(totalLoss, accuracy)
    }


}

 */