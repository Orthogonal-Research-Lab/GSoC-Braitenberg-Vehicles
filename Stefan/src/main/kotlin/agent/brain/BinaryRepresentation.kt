package agent.brain

import flip
import pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Binary representation of various data structures.
 */
@ExperimentalUnsignedTypes
class BinaryRepresentation(val representation: UByteArray) {

    operator fun get(from: Int, to: Int): UByteArray {
        return (representation.sliceArray(IntRange(from, to)))
    }

    operator fun get(at: Int): UByte {
        return representation[at]
    }

    operator fun plus(other: BinaryRepresentation): BinaryRepresentation {
        return BinaryRepresentation(this.representation + other.representation)
    }

    fun flip(pos: Int) {
        check(pos < this.length()) { println("Trying to flip bit out of bounds!") }
        val which = pos / 8
        this.representation[which] = this.representation[which].flip(pos % 8)
    }

    fun crossover(other: BinaryRepresentation, pos: Int = Random.nextInt(this.length())): BinaryRepresentation {
        check(other.length() == this.length()) { throw Exception("Can not crossover two different-length strings!") }
        check(pos < this.length()) { throw Exception("Trying to crossover out of bounds! Requested position was: $pos") }
        val elNumber = pos / elSize
        val e1 = this[elNumber]
        val e2 = other[elNumber]

        val crosspoint = elSize - 1 - pos % elSize
        val oursFirstCP: UByte = (e1.div(2.pow(crosspoint).toUByte()) + e2.rem(2.pow(crosspoint).toUByte())).toUByte()
        val theirFirstCP: UByte =
            (e2.div(2.pow(crosspoint).toUByte()) + e1.rem(2.pow(crosspoint).toUByte())).toUByte()
        val oursFirst: UByteArray =
            (this[0, elNumber - 1] + oursFirstCP) + other[elNumber + 1, this.length() / elSize - 1]
        val theirFirst: UByteArray =
            other[0, elNumber - 1] + theirFirstCP + this[elNumber + 1, this.length() / elSize - 1]
        val childrenVariants = arrayOf(
            oursFirst, theirFirst
        )
        return BinaryRepresentation(childrenVariants.random())
    }


    fun length(): Int {
        return representation.size * 8
    }

    fun toByteArray(): UByteArray {
        return representation.toUByteArray()
    }

    /**
     * Tells how many nodes there are in network based on
     */
    fun nodesSize(): Int {
        val nodesCount =
            (0.5 + sqrt((1 + this.length()).toDouble()) / 2).roundToInt() //2nd degree polynomial n^2 - n - 2c = 0 solution
        return nodesCount
    }

    companion object {
        /**
         * Size of a representation of one element in a matrix.
         */
        const val elSize = 8
    }

}
