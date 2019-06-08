package agent

import Dot
import DoubleVector
import angleToXAxis
import center
import check
import javafx.animation.KeyValue
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape
import sum
import world.WorldObject

class Vehicle(
    val body: Body,
    val motors: Array<Motor>,
    val sensors: Array<Sensor>,
    var speed: DoubleVector
) {
    val render = VehicleRender(body, sensors, motors)
    var oldSpeed: DoubleVector = DoubleVector(0.0, 0.0) //used for rotation computation

    /**
     * Changes velocity and angle of current vehicle, based on sensors affected by objects in the world.
     */
    fun updateMovementVector(affectors: Collection<WorldObject>) {
        var velVec: DoubleVector = this.speed
        oldSpeed = this.speed.copy()
        affectors.forEach {
            val wo = it
            sensors.forEach { ite ->
                velVec += wo.effectOnDistance(
                    ite.x, ite.y
                ) * ite.polarity
            }
        }
    }

    fun angleToX(): Double {
        return angleToXAxis(arrayOf(Dot(0.0, 0.0), Dot(this.speed.x, this.speed.y)))
    }

    /**
     * Used for JavaFX transformation.
     */
    fun rotateAngle(): Double {
        return angleToXAxis(
            arrayOf(
                Dot(0.0, 0.0),
                Dot(this.oldSpeed.x, this.oldSpeed.y)
            )
        ) - this.angleToX()
    }


    inner class VehicleRender(body: Body, sensors: Array<Sensor>, motors: Array<Motor>) : StackPane() {
        val list: MutableList<Shape> = mutableListOf()

        init {
            list.add(body.shape)
            motors.forEach { list.add(it.shape) }
            sensors.forEach { list.add(it.shape) }
        }

        fun center(): Dot {
            return center(body.shape.boundsInParent)
        }

        /**
         * Takes speed and angle now and calculates transformations for each body part
         */
        fun currentUpdate(): Set<KeyValue> {
            val out: MutableSet<KeyValue> = mutableSetOf()
            // rotate body
            out.add(KeyValue(body.shape.rotateProperty(), rotateAngle()))
            // move body
            out.add(KeyValue(body.shape.layoutXProperty(), center().x + speed.x))
            out.add(KeyValue(body.shape.layoutYProperty(), center().y + speed.y))
            // move parts of the body
            val pointRotation: DoubleVector = speed + (-oldSpeed)
            val put = { bp: BodyPart ->
                val shiftX = sum(this.center().x, bp.centerOffset.x, pointRotation.x, speed.x)
                val shiftY = sum(this.center().y, bp.centerOffset.y, pointRotation.y, speed.y)
                //out.add(KeyValue(bp.shape.centerXProperty(), shiftX))
                out.add(KeyValue(bp.shape.layoutYProperty(), shiftY))
            }
            sensors.forEach {
                put(it)
            }
            motors.forEach {
                put(it)
            }
            return out
        }

    }

    companion object Factory {
        /**
         * Rectangular, round sensors, round motors, straight sensors-motors of different polarities.
         */
        fun simpleVehicle(
            leftTopAngleX: Double,
            leftTopAngleY: Double,
            shortSide: Double,
            longSide: Double,
            sensorMotorRadius: Double,
            sensorsDistance: Double,
            speedX: Double,
            speedY: Double
        ): Vehicle {
            check(sensorsDistance <= shortSide) {
                throw IllegalArgumentException("Sensors distance should be shorter than side!")
            }
            val body =
                Body(
                    Rectangle(leftTopAngleX, leftTopAngleY, longSide, shortSide),
                    DoubleVector(0.0, 0.0)
                )
            body.shape.fill = Color.MOCCASIN
            // Rectangle is default positioned with long side horisontally
            val bodyCenter = Dot(leftTopAngleX + longSide / 2, leftTopAngleX - shortSide / 2)
            val sensorRight = Sensor(
                centerOffset = DoubleVector(longSide / 2, -sensorsDistance / 2),
                bodyCenter = bodyCenter,
                polarity = 1
            )
            val sensorLeft = Sensor(
                centerOffset = DoubleVector(longSide / 2, sensorsDistance / 2),
                bodyCenter = bodyCenter,
                polarity = -1
            )
            val motorRight = Motor(
                centerOffset = DoubleVector(-longSide / 2, -sensorsDistance / 2),
                bodyCenter = bodyCenter
            )
            val motorLeft = Motor(
                centerOffset = DoubleVector(-longSide / 2, sensorsDistance / 2),
                bodyCenter = bodyCenter
            )

            return Vehicle(
                body,
                arrayOf(motorLeft, motorRight),
                arrayOf(sensorLeft, sensorRight),
                DoubleVector(speedX, speedY)
            )


        }
    }
}
