package agent

import Dot
import DoubleVector
import javafx.scene.shape.Circle
import javafx.scene.shape.Shape

class Motor(
    bodyCenter: Dot,
    centerOffset: DoubleVector,
    shape: Shape = Circle(
        bodyCenter.x + centerOffset.x,
        bodyCenter.y + centerOffset.y,
        1.0
    )
) : BodyPart(centerOffset = centerOffset, shape = shape) {
    val x
        get() = if (!(shape is Circle)) shape.layoutX else shape.centerX

    val y
        get() = if (!(shape is Circle)) shape.layoutY else shape.centerY

}