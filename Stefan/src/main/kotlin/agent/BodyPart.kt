package agent

import DoubleVector
import javafx.scene.shape.Shape
import tornadofx.*

abstract class BodyPart(
    val shape: Shape,
    var centerOffset: DoubleVector
) {


    fun rotateAroundCenter(angle: Dimension<Dimension.AngularUnits>) {
        this.centerOffset.rotate(angle)
    }
}

