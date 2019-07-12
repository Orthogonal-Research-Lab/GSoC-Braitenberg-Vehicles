package agent

import DoubleVector
import javafx.scene.shape.Circle
import javafx.scene.shape.Shape
import tornadofx.*

abstract class BodyPart(
    val shape: Shape,
    var centerOffset: DoubleVector
) {
    fun getX() = if (shape is Circle) shape.centerX else shape.layoutX
    fun getY() = if (shape is Circle) shape.centerY else shape.layoutY
    fun getXProperty() = if (shape is Circle) shape.centerXProperty() else shape.layoutXProperty()
    fun getYProperty() = if (shape is Circle) shape.centerYProperty() else shape.layoutYProperty()

    fun rotateAroundCenter(angle: Dimension<Dimension.AngularUnits>) {
        this.centerOffset.rotate(angle)
    }
}

