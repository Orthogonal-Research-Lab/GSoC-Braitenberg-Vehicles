package world

import Dot
import DoubleVector
import angleToXAxis
import javafx.scene.shape.Circle
import javafx.scene.shape.Shape
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

/**
 * effectStrength is relative to the vehicle's attraction
 */
class WorldObject(
    val x: Double, val y: Double, val size: Double, val effectStrength: Double,
    val shape: Shape = Circle(x, y, size)
) {

    /**
     * Newton's law. Returns (x, y) acceleration (mass is assumed to be 1 => a=F)
     */
    fun effectOnDistance(toX: Double, toY: Double): DoubleVector {
        val G = 10
        val rSquare = (toX - this.x).pow(2) + (toY - this.y).pow(2)
        val F = G * this.effectStrength * 1000 / (rSquare)
        val alpha = angleToXAxis(
            arrayOf(Dot(x, y), Dot(toX, toY))
        )
        return DoubleVector(F * cos(alpha), F * sin(alpha))
    }

    companion object {
        fun randomWorldObject(
            worldWidth: Double,
            worldHeight: Double,
            effectMin: Double,
            effectMax: Double,
            size: Double
        ): WorldObject {
            return WorldObject(
                Random.nextDouble(worldWidth),
                Random.nextDouble(worldHeight),
                size,
                Random.nextDouble(effectMin, effectMax)
            )
        }
    }
}