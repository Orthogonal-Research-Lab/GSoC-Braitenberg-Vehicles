package agent

import DoubleVector
import Matrix
import bytes
import sum
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import kotlin.math.floor
import kotlin.math.sqrt

open class Network(
    val innerNeurons: Set<Neuron>,
    val inputNeurons: Array<Neuron>,
    val outputNeurons: Array<Neuron>,
    val connections: Set<Edge>
) {

    init {
        (innerNeurons + inputNeurons + outputNeurons).forEach { it.network = this }
    }


    class Neuron(
        var network: Network? = null,
        private val activation: (DoubleVector) -> DoubleVector = { sum: DoubleVector -> sum },
        var signal: DoubleVector? = null
    ) {

        fun shoot(): DoubleVector {
            if (signal != null) {
                return activation(signal!!)
            }
            val incoming = network!!.connections.filter { it.to == this@Neuron }
            return activation(incoming.map { it.from.shoot() * it.weight }.sum())
        }

    }

    /**
     * Propagate the signal and get the vector output for motors
     */
    fun propagate(signal: Array<DoubleVector>): Array<DoubleVector> {
        check(signal.size == inputNeurons.size) {
            throw IllegalArgumentException("Signal dimension must be equal to the input layer dimension!")
        }
        // supply signal to input neurons
        signal.withIndex().forEach { (idx, e) ->
            this.inputNeurons[idx].signal = e
        }
        // output (motor) neurons "ask back" for the signal (ensures backing neurons are ready)
        val out = outputNeurons.map { it.shoot() }.toTypedArray()
        return out
    }

    /**
     * Binary representation of network.
     */
    fun toBinary(): BitSet {
        check(this.inputNeurons.size == this.outputNeurons.size && this.inputNeurons.size == 2) {
            throw Exception("Output and input layers should always be 2 neurons long!")
        }
        val nodeCount = this.innerNeurons.size + 4 //input and output are always two!
        val innerOrdered = innerNeurons.toTypedArray()
        val neuronAccessor = { idx: Int ->
            when (idx) {
                in 0..1 -> inputNeurons[idx]
                in 2 until nodeCount - 2 -> innerOrdered[idx]
                in nodeCount - 2 until nodeCount -> outputNeurons[idx]
                else -> Neuron()
            }
        }
        var representation = byteArrayOf()
        for (j in 0 until nodeCount) {
            for (i in 0 until j) {
                if (connections.any { it.from == neuronAccessor(i) && it.to == neuronAccessor(j) }) {
                    val elBytes =
                        connections.filter { it.from == neuronAccessor(i) && it.to == neuronAccessor(j) }[0].weight.bytes()
                    representation += elBytes
                }
            }
        }
        //type conversions
        return BitSet.valueOf(representation)
    }

    companion object Factory {

        /**
         * 1 hidden layer, 2*2d input and output.
         */
        fun smallAutoencoder(): Network {
            val (i1, i2) = arrayOf(Neuron(), Neuron())
            val h1 = Neuron()
            val (o1, o2) = arrayOf(Neuron(), Neuron())
            val connections = setOf(
                Edge(i1, h1, 1.0),
                Edge(i2, h1, 1.0),
                Edge(h1, o1, 0.4),
                Edge(h1, o2, 0.6)
            )
            return Network(setOf(h1), arrayOf(i1, i2), arrayOf(o1, o2), connections)
        }

        /**
         * Build network from the sequence of bits. In this version, weights are represented by 32bit
         */
        fun fromBinary(representation: BitSet): Network {
            check(representation.size() % 32 == 0) { throw Exception("Network positions should be represented by 32 bit chunks!") }
            val nodeCount = sqrt(representation.size().toDouble())
            check((nodeCount - floor(nodeCount)) == 0.0) { throw Exception("Bit representation length should be an integer square!") }
            val adjMatrix = Matrix<Double>(nodeCount.toInt(), nodeCount.toInt())

            for (j in 0 until nodeCount.toInt()) {
                for (i in 0 until j) {
                    val chStart = (i + j - 2)
                    val el = ByteBuffer.wrap(representation[chStart, chStart + 32].toByteArray())
                        .order(ByteOrder.LITTLE_ENDIAN).double
                    adjMatrix[i, j] = el
                }
            }
            return fromAdjMatrix(adjMatrix)
        }

        /**
         * "From" along x axis and "to" along y.
         */
        fun fromAdjMatrix(matrix: Matrix<Double?>): Network {
            check(matrix.isUpperHalfFree()) { throw Exception("Only values under diagonal are allowed!") }
            val inputNeurons = arrayOf(Neuron(), Neuron())
            val innerNeurons = mutableListOf<Neuron>()
            for (inidx in 2 until matrix.xSize - 2) { //convention: first two and last two neurons are input/output
                innerNeurons.add(Neuron())
            }
            val outputNeurons = arrayOf(Neuron(), Neuron())
            val neuronAccessor = { idx: Int ->
                when (idx) {
                    in 0..1 -> inputNeurons[idx]
                    in 2 until matrix.xSize - 2 -> innerNeurons[idx]
                    in matrix.xSize - 2 until matrix.xSize -> outputNeurons[idx]
                    else -> Neuron()
                }
            }
            var connections = mutableListOf<Edge>()
            for (i in 0 until matrix.xSize) {
                for (j in i until matrix.ySize) {
                    if (matrix[i, j] != 0.0)
                        connections.add(Edge(neuronAccessor(i), neuronAccessor(j), matrix[i, j] ?: 0.0))
                }
            }
            return Network(innerNeurons.toSet(), inputNeurons, outputNeurons, connections.toSet())
        }

    }

}