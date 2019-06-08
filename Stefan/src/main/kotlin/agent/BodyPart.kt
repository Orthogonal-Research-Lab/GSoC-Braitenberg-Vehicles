package agent

import Dot
import DoubleVector
import check
import javafx.scene.shape.Circle
import javafx.scene.shape.Shape

interface BodyPart {
    val shape: Shape
    val centerOffset: DoubleVector
}

class Body(
    override val shape: Shape,
    override val centerOffset: DoubleVector
) : BodyPart {
}

class Motor(
    bodyCenter: Dot,
    override val centerOffset: DoubleVector,
    override val shape: Shape = Circle(bodyCenter.x + centerOffset.x, bodyCenter.y + centerOffset.y, 1.0)
) : BodyPart {
    val x
        get() = if (!(shape is Circle)) shape.layoutX else shape.centerX

    val y
        get() = if (!(shape is Circle)) shape.layoutY else shape.centerY

    fun move(processedSenses: FloatArray): FloatArray {
        return FloatArray(0)
    }
}

class Sensor(
    bodyCenter: Dot,
    override val centerOffset: DoubleVector,
    override val shape: Shape = Circle(bodyCenter.x + centerOffset.x, bodyCenter.y + centerOffset.y, 1.0),
    val polarity: Int
) : BodyPart {
    val x
        get() = if (!(shape is Circle)) shape.layoutX else shape.centerX

    val y
        get() = if (!(shape is Circle)) shape.layoutY else shape.centerY

    init {
        check(polarity == 1 || polarity == -1) { throw IllegalArgumentException("Polarity must be 1 or -1!") }
    }

    fun feel(signal: FloatArray): FloatArray {
        return FloatArray(0)
    }
}