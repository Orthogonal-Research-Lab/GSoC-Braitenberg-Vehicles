package agent.brain

import bytes
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Binary representation of various data structures.
 */
class BinaryRepresentation(val representation: BitSet) {

    operator fun get(from: Int = 0, to: Int = representation.size()): BinaryRepresentation {
        return BinaryRepresentation(representation[from, to])
    }

    operator fun plus(other: BinaryRepresentation): BinaryRepresentation {
        return BinaryRepresentation(BitSet.valueOf(this.representation.toByteArray() + other.representation.toByteArray()))
    }

    fun flip(pos: Int) {
        check(pos < this.length()) { println("Trying to flip bit out of bounds!") }
        this.representation.flip(pos)
    }

    fun length(): Int {
        return representation.size()
    }

    fun toByteArray(): ByteArray {
        return representation.toByteArray()!!
    }

    /**
     * Tells how many nodes there are in network based on
     */
    fun nodesSize(): Int {
        check((representation.length() % chunkSize) == 0) {
            throw Exception(
                "There should be a whole " +
                        "amount of chunks in the representation! Was replength: ${representation.length()}, chunk size $chunkSize," +
                        " mod ${representation.length() % chunkSize}"
            )
        }
        val chunksCount = this.length() / chunkSize
        val nodesCount =
            (0.5 + sqrt((1 + 8 * chunksCount).toDouble()) / 2).roundToInt() //2nd degree polynomial n^2 - n - 2c = 0 solution
        return nodesCount
    }

    companion object {

        val chunkSize = 64

        fun valueOf(arr: ByteArray): BinaryRepresentation {
            return BinaryRepresentation(BitSet.valueOf(arr))
        }

        fun of(d: Double) = valueOf(d.bytes())

    }

}