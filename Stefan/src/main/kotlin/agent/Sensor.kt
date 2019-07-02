package agent

import Dot
import DoubleVector
import check
import javafx.scene.shape.Circle
import javafx.scene.shape.Shape
import model.WorldObject

class Sensor(
    bodyCenter: Dot,
    centerOffset: DoubleVector,
    shape: Shape = Circle(
        bodyCenter.x + centerOffset.x,
        bodyCenter.y + centerOffset.y,
        1.0
    ),
    val polarity: Int
) : BodyPart(centerOffset = centerOffset, shape = shape) {
    val x
        get() = if (!(shape is Circle)) shape.layoutX else shape.centerX

    val y
        get() = if (!(shape is Circle)) shape.layoutY else shape.centerY

    init {
        check(polarity == 1 || polarity == -1) { throw IllegalArgumentException("Polarity must be 1 or -1!") }
    }

    fun percept(affectors: Collection<WorldObject>): DoubleVector {
        var out = DoubleVector(0.0, 0.0)
        affectors.forEach {
            out += it.effectOnDistance(this.x, this.y) * (this.polarity.toDouble())
        }
        return out
    }

}