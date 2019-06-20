package agent

import DoubleVector
import sum

/**
 * Represents a single neuron in the brain. Has knowledge of the parents and its child index in it.
 */
abstract class Neuron {

    fun activation(input: DoubleVector): DoubleVector {
        //identity
        return input
    }

    abstract fun processSignal(): Array<DoubleVector>
}


class NormalizedInputNeuron(
    val weightsOutgoing: Array<DoubleVector>,
    var signal: DoubleVector = DoubleVector(1.0, 1.0)
) : Neuron() {

    override fun processSignal(): Array<DoubleVector> {
        val out = arrayOfNulls<DoubleVector>(weightsOutgoing.size)
        for (i in 0 until weightsOutgoing.size) {
            out[i] = signal * weightsOutgoing[i]
        }
        return out.requireNoNulls()
    }

}

class NormalizedInnerNeuron(val goesFrom: Set<Pair<Neuron, Int>>, private val weightsOutgoing: Array<DoubleVector>) :
    Neuron() {

    init {
        println(weightsOutgoing.sum()) //dbg
        //check(weightsOutgoing.sum().elements.any { it != 1.0 })
        //{ throw IllegalArgumentException("Sum of weights along a weight vector axis should be 1!") }
    }

    /**
     * Aggregates signal from parent neurons.
     */
    fun accept(goesFrom: Set<Pair<Neuron, Int>>): DoubleVector {
        var signal = DoubleVector(0.0, 0.0)
        goesFrom.forEach { (n, idx) ->
            //check(n is InnerNeuron) { throw InvalidObjectException("Output neuron connected to an output neuron!") }
            signal += n.processSignal()[idx]
        }
        return activation(signal)
    }

    override fun processSignal(): Array<DoubleVector> {
        val signal = accept(goesFrom)
        val out = arrayOfNulls<DoubleVector>(weightsOutgoing.size)
        for (i in 0 until weightsOutgoing.size) {
            out[i] = signal * weightsOutgoing[i]
        }
        return out.requireNoNulls()
    }

}

class OutputNeuron(val goesFrom: Set<Pair<Neuron, Int>>) : Neuron() {

    /**
     * Aggregates signal from parent neurons.
     */
    fun accept(goesFrom: Set<Pair<Neuron, Int>>): DoubleVector {
        var signal = DoubleVector(0.0, 0.0)
        goesFrom.forEach { (n, idx) ->
            //check(n is InnerNeuron) { throw InvalidObjectException("Output neuron connected to an output neuron!") }
            signal += n.processSignal()[idx]
        }
        return activation(signal)
    }

    override fun processSignal(): Array<DoubleVector> {
        return arrayOf(accept(goesFrom))
    }
}

