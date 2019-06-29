package agent

import DoubleVector
import javafx.scene.shape.Shape

/**
 * Center offset is the offset of center from the layoutX/Y.
 */
class Body(
    shape: Shape,
    centerOffset: DoubleVector
) : BodyPart(shape, centerOffset) {
}