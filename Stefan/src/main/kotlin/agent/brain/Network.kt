package agent.brain

import DoubleVector
import Matrix
import bytes
import generateNormalizedSequence
import sum
import java.nio.ByteBuffer
import java.nio.ByteOrder

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
    fun toBinary(): BinaryRepresentation {
        check(this.inputNeurons.size == this.outputNeurons.size && this.inputNeurons.size == 2) {
            throw Exception("Output and input layers should always be 2 neurons long!")
        }
        val nodeCount = this.innerNeurons.size + Factory.SURF_NEURONS_COUNT * 2
        val na = NeuronAdapter(
            inputNeurons, innerNeurons.toTypedArray(), outputNeurons, nodeCount,
            Factory.SURF_NEURONS_COUNT
        )

        var representation = byteArrayOf()
        for (j in 0 until nodeCount) {
            for (i in 0 until j) {
                if (connections.any { it.from == na[i] && it.to == na[j] }) {
                    val elBytes =
                        connections.filter { it.from == na[i] && it.to == na[j] }[0].weight.bytes()
                    representation += elBytes
                } else {
                    representation += 0.0.bytes()
                }
            }
        }
        //type conversions
        null
        return BinaryRepresentation.valueOf(representation)
    }

    companion object Factory {

        const val SURF_NEURONS_COUNT = 2

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
        fun fromBinary(representation: BinaryRepresentation): Network {
            println("Representation length: ${representation.length()}")
            val nodeCount = representation.nodesSize()
            val adjMatrix = Matrix<Double>(nodeCount, nodeCount)

            for (j in 1 until nodeCount) {
                for (i in 0 until j) {
                    val chunkPos = (j - 1) * j / 2 + i
                    val el =
                        ByteBuffer.wrap(representation[chunkPos * BinaryRepresentation.chunkSize, (chunkPos + 1) * BinaryRepresentation.chunkSize].toByteArray())
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
            val na =
                NeuronAdapter(
                    inputNeurons,
                    innerNeurons.toTypedArray(),
                    outputNeurons,
                    matrix.xSize,
                    SURF_NEURONS_COUNT
                )
            val connections = mutableListOf<Edge>()
            for (i in 0 until matrix.xSize) {
                for (j in i + 1 until matrix.ySize) {
                    if (matrix[i, j] != 0.0)
                        connections.add(Edge(na[i], na[j], matrix[i, j] ?: 0.0))
                }
            }
            return Network(innerNeurons.toSet(), inputNeurons, outputNeurons, connections.toSet())
        }

        /**
         * Generate circuit-free network with random normalized weights of a given length.
         */
        fun generateRandomOfSize(brainSize: Int): Network {
            val adjMatrix = Matrix<Double>(brainSize, brainSize)
            for (i in 0 until brainSize) { // walk "from" columns
                val thisNodeWeights = generateNormalizedSequence(size = brainSize - i)
                val it = thisNodeWeights.iterator()
                for (j in i + 1 until brainSize) {
                    val el = it.next()
                    adjMatrix[i, j] = el
                }
            }
            println("Generated network: $adjMatrix")
            return fromAdjMatrix(adjMatrix)
        }

    }

}

/**
 * Makes accessing various neuron layers easier.
 */
class NeuronAdapter(
    val inputNeurons: Array<Network.Neuron>,
    val innerNeurons: Array<Network.Neuron>,
    val outputNeurons: Array<Network.Neuron>,
    val nodeCount: Int,
    surfaceNeurons: Int
) {
    val COUNT_SURF_NEURONS = surfaceNeurons

    operator fun get(idx: Int): Network.Neuron {
        return when (idx) {
            in 0 until COUNT_SURF_NEURONS -> inputNeurons[idx]
            in COUNT_SURF_NEURONS until nodeCount - COUNT_SURF_NEURONS -> innerNeurons[idx - COUNT_SURF_NEURONS]
            in nodeCount - COUNT_SURF_NEURONS until nodeCount -> outputNeurons[idx - nodeCount + COUNT_SURF_NEURONS]
            else -> Network.Neuron()
        }
    }
}