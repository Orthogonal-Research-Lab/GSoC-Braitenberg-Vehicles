package model

import Dot
import DoubleVector
import angleToXAxis
import javafx.scene.Group
import javafx.scene.shape.Circle
import view.WorldObjectGroup
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

/**
 * effectStrength is relative to the vehicle's attraction
 */
class WorldObject(
    val x: Double, val y: Double, val size: Double, val effectStrength: Double,
    val shape: Group = WorldObjectGroup(Circle(x, y, size))
) {

    /**
     * Newton's law. Returns (x, y) acceleration (mass is assumed to be 1 => a=F)
     */
    fun effectOnDistance(toX: Double, toY: Double): DoubleVector {
        val G = 10
        var rSquare = (toX - this.x).pow(2) + (toY - this.y).pow(2)
        if (rSquare == 0.0)
            rSquare = 0.0001
        val F = G * this.effectStrength * 100 / (rSquare)
        val alpha = angleToXAxis(
            Dot(x, y), Dot(toX, toY)
        )
        return DoubleVector(F * cos(alpha), -F * sin(alpha)) // y to top <=> y negative
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
                Random.nextDouble(worldWidth - size),
                Random.nextDouble(worldHeight - size),
                size,
                Random.nextDouble(effectMin, effectMax)
            )
        }

        fun randomWorldObjects(
            worldWidth: Double,
            worldHeight: Double,
            effectMin: Double,
            effectMax: Double,
            size: Double,
            many: Int
        ): MutableSet<WorldObject> {
            val worldObjects: MutableSet<WorldObject> = mutableSetOf()
            for (i in 1..many) worldObjects.add(
                randomWorldObject(
                    worldWidth,
                    worldHeight,
                    effectMin,
                    effectMax,
                    size
                )
            )
            return worldObjects
        }

    }
}