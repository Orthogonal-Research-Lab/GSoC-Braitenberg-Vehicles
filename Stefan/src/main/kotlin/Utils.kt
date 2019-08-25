import javafx.geometry.Bounds
import javafx.util.StringConverter
import tornadofx.*
import view.InfoFragment
import java.io.File
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.*
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.streams.asSequence

/**
 * This class is more for trying around, than bringing real value.
 */

tailrec fun fac(to: Int): Int {
    return (if (to == 1) 1 else fac(to - 1))
}

/**
 * Validation for boolean predicates.
 */
inline fun check(value: Boolean, lazyMessage: () -> Any): Unit {
    if (!value) {
        val message = lazyMessage()
        throw IllegalStateException(message.toString())
    }
}

fun angleToXAxis(l1d1: Dot, l1d2: Dot = Dot(0.0, 0.0)): Double {
    val alpha = atan2((l1d2.y - l1d1.y), (l1d2.x - l1d1.x)) //radian = slope
    return (if (alpha > 0.0) alpha else alpha + 2 * PI)
}

data class Dot(val x: Double, val y: Double)

class DoubleVector(vararg elements: Double) {

    val length
        get() = sqrt(elements.map { it * it }.sum())

    var elements: DoubleArray = doubleArrayOf(*elements)
    var x = elements[0]
        get() = elements[0]
        set(value) {
            elements[0] = value
            field = value
        }

    var y = elements[1]
        get() = elements[1]
        set(value) {
            elements[1] = value
            field = value
        }

    fun rotate(theta: Dimension<Dimension.AngularUnits>) {
        check(elements.size == 2) { throw IllegalArgumentException("Can rotate only 2d arrays!") }
        val (x, y) = elements
        // angle transformation multiplication
        val rotAngle: Double = if (theta.units == Dimension.AngularUnits.deg) theta.value * PI / 180 else theta.value
        val xTick = x * cos(rotAngle) - y * sin(rotAngle)
        val yTick = y * cos(rotAngle) + x * sin(rotAngle)
        this.elements = doubleArrayOf(xTick, yTick)
    }

    operator fun plus(vector: DoubleVector): DoubleVector {
        check(this.elements.size == vector.elements.size) { throw Exception("Can't add vectors of different lengths!") }
        var out = DoubleArray(this.elements.size)
        for (i in 0 until max(elements.size, vector.elements.size)) {
            when {
                i > vector.elements.size ->
                    out[i] = this.elements[i]
                i > this.elements.size ->
                    out[i] = vector.elements[i]
                else -> out[i] = vector.elements[i] + this.elements[i]
            }
        }
        return DoubleVector(*out)
    }


    fun vecLength(): Double {
        return sqrt(elements.map { it * it }.sum())
    }


    operator fun unaryMinus() = run {
        var out = DoubleArray(this.elements.size)
        for (i in 0 until elements.size) {
            out[i] = -this.elements[i]
        }
        DoubleVector(*out)
    }

    operator fun times(vector: DoubleVector): DoubleVector {
        check(this.elements.size == vector.elements.size) { throw Exception("Can't add vectors of different lengths!") }
        var out = DoubleArray(this.elements.size)
        for (i in 0 until elements.size) {
            out[i] = this.elements[i] * vector.elements[i]
        }
        return DoubleVector(*out)
    }

    operator fun times(c: Double): DoubleVector {
        var out = DoubleArray(this.elements.size)
        for (i in 0 until elements.size) {
            out[i] = this.elements[i] * c
        }
        return DoubleVector(*out)
    }

    /**
     * Vector product
     */
    infix fun x(o: DoubleVector) : DoubleVector {
        require(this.length <= 3 && o.length <= 3)
        val (u1, u2, u3) = arrayOf(elements[0], elements[1], elements[2])
        val (v1, v2, v3) = arrayOf(o.elements[0], o.elements[1], o.elements[2])
        return DoubleVector(u2 * v3 - v2 * u3,
            v1 * u3 - u1 * v3,
            u1 * v2 - v1 * u2)
    }

    fun copy(): DoubleVector {
        return DoubleVector(*this.elements.copyOf())
    }

    override fun toString(): String {
        val sb: StringBuilder = StringBuilder()
        sb.append("( ")
        elements.forEach { sb.append("${it} ") }
        sb.append(")")
        return String(sb)
    }
}

fun Collection<DoubleVector>.sum(): DoubleVector {
    var out = DoubleVector(0.0, 0.0)
    this.forEach { out += it }
    return out
}

fun sum(vararg elements: Double): Double {
    var out = 0.0
    elements.forEach { out += it }
    return out
}

fun prod(vararg elements: Double): Double {
    var out = 0.0
    elements.forEach { out *= it }
    return out
}


/**
 * Radian to degrees conversion
 */
fun Double.degrees(): Double {
    return this * 180 / PI
}

fun Array<DoubleVector>.sum(): DoubleVector {
    var out = this[0]
    for (i in 1 until this.size) out += this[i]
    return out
}

/**
 * Matrix class.
 */
class Matrix<T>(val xSize: Int, val ySize: Int, val array: Array<Array<T>>) {

    companion object {

        inline operator fun <reified T> invoke() = Matrix(0, 0, Array(0) { emptyArray<T>() })

        inline operator fun <reified T> invoke(xWidth: Int, yWidth: Int) =
            Matrix(xWidth, yWidth, Array(xWidth, { arrayOfNulls<T>(yWidth) }))

        inline operator fun <reified T> invoke(xWidth: Int, yWidth: Int, operator: (Int, Int) -> (T)): Matrix<T> {
            val array = Array(xWidth) {
                val x = it
                Array(yWidth) { el -> operator(x, el) }
            }
            return Matrix(xWidth, yWidth, array)
        }
    }

    operator fun get(x: Int, y: Int): T {
        return array[x][y]
    }

    operator fun set(x: Int, y: Int, t: T) {
        array[x][y] = t
    }

    inline fun forEach(operation: (T) -> Unit) {
        array.forEach { it.forEach { operation.invoke(it) } }
    }

    inline fun forEachIndexed(operation: (x: Int, y: Int, T) -> Unit) {
        array.forEachIndexed { x, p -> p.forEachIndexed { y, t -> operation.invoke(x, y, t) } }
    }

    fun isUpperHalfFree(): Boolean {
        for (i in 0 until ySize) {
            for (j in i until xSize)
                if (this[j, i] != null) return false
        }
        return true
    }

    override fun toString(): String {
        val sb = StringBuilder()

        sb.append("\n\t")
        for (i in 1 until array.size + 1) sb.append("$i\t") // x axis index ("from")
        sb.append("\n")
        for (j in 0 until array[0].size){
            sb.append("${j + 1}\t")// y axis index ("to")
            for (i in 0 until array.size) {
                val num = if (this[i,j] is Double) (this[i,j] as Double).round(2) else this[i,j].toString()
                sb.append("$num\t")
            }
            sb.append("\n")
        }
        return sb.toString()
    }


}

fun Double.bytes(): ByteArray =
    ByteBuffer.allocate(java.lang.Long.BYTES)
        .putLong(java.lang.Double.doubleToLongBits(this)).array()

fun generateNormalizedSequence(
    from: Double = 0.0,
    to: Double = 1.0,
    size: Int,
    normalize: Boolean = true
): Collection<Double> {
    var out = mutableListOf<Double>()
    for (i in 0 until size) {
        out.add(Random.nextDouble(from, to))
    }
    if (normalize) out = out.map { it / out.sum() }.toMutableList()
    return out
}

@ExperimentalUnsignedTypes
fun UByteArray.mapInPlace(transform: (UByte) -> UByte) {
    for (i in this.indices) {
        this[i] = transform(this[i])
    }
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

fun UByte.flip(bitIdx: Int): UByte {
    check(bitIdx in 0..7) { throw Exception("UByte has only 8 bits!") }
    var op = 1.toUByte()
    op = op.toInt().shl(7 - bitIdx).toUByte()
    return this.xor(op)
}


fun Int.pow(i: Int): Int {
    var out = 1
    for (j in 1..i) out *= j
    return out
}

fun <T> Array<T>.random(): T = this[Random.nextInt(this.size)]

fun mean(vararg elements: Double): Double {
    return (elements.sum() / elements.size)
}

val pathsep = File.separator

fun path(vararg pathSlices: String): String {
    val sb = java.lang.StringBuilder()
    pathSlices.forEach { sb.append(it + pathsep) }
    val out = sb.toString()
    return out.substring(0, out.length - 1)
}

/**
 * Kotlin style on-demand function to fetch an instance of fragment singleton
 */
fun <R, S : InfoFragment<R>> openFragment(fragmentClass: KClass<S>, infos: R) {
    val fragment = find(fragmentClass)
    fragment.openModal(block = true)
}

class EpochInfoConverter : StringConverter<Int>() {
    override fun toString(`object`: Int?): String {
        return "Epoch count: $`object`"
    }

    override fun fromString(string: String?): Int {
        return string!!.replace("[^\\d]", "").toInt()
    }

}

fun alphaNumericId(outputStrLength: Int): String {
    val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
    return java.util.Random().ints(outputStrLength.toLong(), 0, allowedChars.length)
        .asSequence()
        .map(allowedChars::get)
        .joinToString("")
}

const val FITNESS_TICKS_MEMORY_LEN = 3


