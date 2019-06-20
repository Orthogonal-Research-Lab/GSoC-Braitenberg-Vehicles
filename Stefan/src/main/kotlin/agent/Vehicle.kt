package agent

import Dot
import DoubleVector
import angleToXAxis
import check
import degrees
import javafx.animation.KeyValue
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import model.SimModel
import model.WorldObject
import sum
import tornadofx.*
import kotlin.math.abs
import kotlin.math.log10

class Vehicle(
    val body: Body,
    val motors: Array<Motor>,
    val sensors: Array<Sensor>,
    var speed: DoubleVector
) {
    val bodyParts: MutableSet<BodyPart>
    var oldSpeed: DoubleVector = DoubleVector(0.0, 0.0) //used for rotation computation

    init {
        bodyParts = mutableSetOf(body)
        bodyParts.addAll(motors)
        bodyParts.addAll(sensors)
    }

    fun getAngle() = angleToXAxis(Dot(this.speed.x, this.speed.y))
    fun getX() = this.body.shape.layoutX
    fun getY() = this.body.shape.layoutY


    /**
     * Changes velocity and dAngle of current vehicle, based on sensors affected by objects in the world.
     */
    fun updateSpeed(affectors: Collection<WorldObject>) {
        // save for angle computation
        this.oldSpeed = this.speed.copy()
        sensors.forEach {
            this.speed += it.percept(affectors)
        }
        repulseFromWalls()
    }

    /**
     * Repulses vehicle off the wall, when it is close
     */
    private fun repulseFromWalls() {
        val aspiredX = this.getX() + this.speed.x
        val aspiredY = this.getY() + this.speed.y

        val (fromLeft, fromUp) = arrayOf(this.getX(), this.getY())
        if (fromLeft == 0.0) return //just initialized
        val (fromRight, fromDown) = arrayOf(abs(SimModel.worldEnd.x - fromLeft), abs(SimModel.worldEnd.y - fromUp))
        // truncate speed vectors to out of bounds
        if (aspiredX > SimModel.worldEnd.x) this.speed.x = (SimModel.worldEnd.x - this.getX()) * 0.9
        if (aspiredY > SimModel.worldEnd.y) this.speed.y = (SimModel.worldEnd.y - this.getY()) * 0.9
        // TODO half-Morse potential?
        this.speed.x += abs(log10(fromLeft)) - abs(log10(fromRight)) // add in the positive x, substract in negative (fromRight
        this.speed.y += abs(log10(fromUp)) - abs(log10(fromDown))
    }

    /**
     * Next rotation angle, given in radians.
     */
    fun rotationAngle(): Double {
        // Delta of the angles of old and current speed vector
        return this.getAngle() - angleToXAxis(
            Dot(this.oldSpeed.x, this.oldSpeed.y)
        )
    }


    /**
     * Takes speed and dAngle now and calculates transformations for each body part
     */
    fun calcCurrentUpdate(affectors: MutableSet<WorldObject>): Set<KeyValue> {
        this.updateSpeed(affectors)
        val out: MutableSet<KeyValue> = mutableSetOf()
        // rotate body
        val rotateAngle = this.rotationAngle()
        out.add(KeyValue(body.shape.rotateProperty(), body.shape.rotate + rotateAngle.degrees()))
        // move parts of the body
        this.body.centerOffset.rotate(rotateAngle.rad)
        val put = { bp: BodyPart ->
            val shape: Circle = bp.shape as Circle //TODO generalize cast
            val oldBPOffset = bp.rotateAroundCenter(rotateAngle.rad)
            // center offset is already angle updated
            val shiftX = sum(this.speed.x, -oldBPOffset.x, bp.centerOffset.x) //+radius
            val shiftY = sum(this.speed.y, -oldBPOffset.y, bp.centerOffset.y)
            out.add(KeyValue(shape.translateXProperty(), shiftX))
            out.add(KeyValue(shape.translateYProperty(), shiftY))
        }
        this.sensors.forEach {
            put(it)
        }
        this.motors.forEach {
            put(it)
        }
        // move body
        out.add(KeyValue(body.shape.translateXProperty(), this.speed.x))
        out.add(KeyValue(body.shape.translateYProperty(), this.speed.y))
        return out
    }

    companion object Factory {
        /**
         * Rectangular, round sensors, round motors, straight sensors-motors of different polarities.
         */
        fun simpleVehicle(
            leftTopX: Double,
            leftTopY: Double,
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
                    Rectangle(leftTopX, leftTopY, longSide, shortSide),
                    DoubleVector(longSide / 2, shortSide / 2)
                )
            body.shape.fill = Color.MOCCASIN
            // Rectangle is default positioned with long side horisontally
            val bodyCenter = Dot(leftTopX + longSide / 2, leftTopY + shortSide / 2)
            val sensorRight = Sensor(
                centerOffset = DoubleVector(-longSide / 2, -sensorsDistance / 2),
                bodyCenter = bodyCenter,
                polarity = 1
            )
            val sensorLeft = Sensor(
                centerOffset = DoubleVector(-longSide / 2, sensorsDistance / 2),
                bodyCenter = bodyCenter,
                polarity = -1
            )
            val motorRight = Motor(
                centerOffset = DoubleVector(longSide / 2, -sensorsDistance / 2),
                bodyCenter = bodyCenter
            )
            val motorLeft = Motor(
                centerOffset = DoubleVector(longSide / 2, sensorsDistance / 2),
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
