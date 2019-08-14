package agent

import Dot
import DoubleVector
import agent.brain.Network
import angleToXAxis
import check
import degrees
import javafx.animation.KeyValue
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import model.SimModel
import model.WorldObject
import presenter.SimPresenter
import alphaNumericId
import sum
import tornadofx.*
import view.VehicleGroup
import java.util.*
import kotlin.math.abs
import kotlin.random.Random

@ExperimentalUnsignedTypes
class Vehicle(
    private val body: Body,
    private val motors: Array<Motor>,
    private val sensors: Array<Sensor>,
    speed: DoubleVector,
    var brain: Network
) {
    private val presenter = find(SimPresenter::class)
    private val model: SimModel by SimModel
    val id = alphaNumericId(10)

    val render = initRender()

    fun initRender(): VehicleGroup {
        val out = VehicleGroup(sensors.map { it.shape } + motors.map { it.shape } + listOf<Node>(body.shape))
        out.onMouseClicked = EventHandler { this.showVehicleInformation() }
        return out
    }

    private fun showVehicleInformation() {
        presenter.showVehicleInformation(this)
    }

    var speed: DoubleVector = speed
        get() = speedArchive[0]

    var speedLastTurn: DoubleVector = DoubleVector(0.0, 0.0)
        get() = speedArchive[1]

    private var speedArchive = LinkedList<DoubleVector>()

    init{
        speedArchive.addFirst(speed)
    }

    private fun getAngle() = angleToXAxis(Dot(this.speed.x, this.speed.y))
    private fun getLayoutX() = this.body.shape.layoutX
    private fun getLayoutY() = this.body.shape.layoutY
    private fun getX() = (this.body.shape as Rectangle).x + this.getLayoutX()
    private fun getY() = (this.body.shape as Rectangle).y + this.getLayoutY()


    /**
     * Changes velocity and dAngle of current vehicle, based on sensors affected by objects in the world.
     */
    fun updateSpeedsArchive(affectors: Collection<WorldObject>) {
        if (this.speedArchive.size >= 5) { //deque filled, make space on the end
            this.speedArchive.removeLast()
        }
        this.speedArchive.addFirst(this.perceptEffects(affectors))
    }


    private fun perceptEffects(affectors: Collection<WorldObject>): DoubleVector {
        val sensorInput = this.sensors.map { it.percept(affectors) }
        val motorOutput = this.brain.propagate(sensorInput.toTypedArray())
        val pureSpeed = motorOutput.sum()
        val adjustedSpeed = repulseFromWalls(pureSpeed)
        return adjustedSpeed
    }

    /**
     * Repulses vehicle off the wall, when it is close
     */
    private fun repulseFromWalls(speed: DoubleVector): DoubleVector {
        val (fromLeft, fromUp) = arrayOf(abs(this.getX()), abs(this.getY()))
        if (fromLeft == 0.0 || fromUp == 0.0) return speed//just initialized
        val (fromRight, fromDown) = arrayOf(abs(model.worldEnd.x - fromLeft), abs(model.worldEnd.y - fromUp))
        // truncate speed vectors to out of bounds
        val out = adjustSpeedInLimits(speed, arrayOf(fromLeft, fromUp, fromRight, fromDown))
        val c = 100.0
        val adjustedSpeed = DoubleVector(
            out.x + repulseFun(fromLeft, c) - repulseFun(fromRight, c)
            , out.y + repulseFun(fromUp, c) - repulseFun(fromDown, c)
        )
        return adjustedSpeed
    }

    private fun adjustSpeedInLimits(speed: DoubleVector, distances: Array<Double>): DoubleVector {
        val out = speed
        val (fromLeft, fromUp, fromRight, fromDown) = distances
        if (fromLeft + speed.x > model.worldEnd.x) out.x = (model.worldEnd.x - fromLeft) * 0.9
        else if (fromRight + speed.x < 0) out.x = (fromRight - 0) * 0.9
        if (fromUp + speed.y > model.worldEnd.y) out.y = (model.worldEnd.y - fromUp) * 0.9
        else if (fromDown + speed.y < 0) out.x = (fromDown - 0) * 0.9
        return out
    }

    /**
     * c is "repulse closer than points" parameter
     */
    fun repulseFun(distance: Double, c: Double): Double {
        if (abs(distance) > c) return 0.0
        else return abs(1 / abs(distance / c))
    }

    /**
     * GA survival fitness of an individual vehicle
     */
    fun fitness(): Double {
        //Area under speeds archive polygon
        // TODO correct calculation for self-intersecting polygons
        val xCoords = this.speedArchive.map { it.x }
        val yCoords = this.speedArchive.map { it.y }
        var areaUnderPolygon = 0.0
        for (i in 0 until xCoords.size - 1) {
            areaUnderPolygon += xCoords[i]*yCoords[i+1] - xCoords[i + 1]*yCoords[i]
        }
        return areaUnderPolygon
    }


    /**
     * Next rotation angle, given in radians.
     */
    fun rotationAngle(): Double {
        // Delta of the angles of old and current speed vector
        val angleNow = this.getAngle()
        val anglePrev = angleToXAxis(
            Dot(this.speedLastTurn.x, this.speedLastTurn.y)
        )
        return angleNow - anglePrev
    }


    /**
     * Takes speed and dAngle now and calculates transformations for each body part
     */
    fun calcCurrentUpdate(affectors: MutableSet<WorldObject>): Set<KeyValue> {
        this.updateSpeedsArchive(affectors)
        val animation = animationChanges()
        return animation
    }

    fun animationChanges(): Set<KeyValue> {
        val out: MutableSet<KeyValue> = mutableSetOf()
        out.add(this.bodyRotation())
        out.addAll(this.moveBodyParts())
        out.addAll(this.moveBody())
        return out
    }

    private fun moveBody(): Collection<KeyValue> {
        val out: MutableSet<KeyValue> = mutableSetOf()
        out.add(KeyValue(body.shape.layoutXProperty(), this.getLayoutX() + this.speed.x))
        out.add(KeyValue(body.shape.layoutYProperty(), this.getLayoutY() + this.speed.y))
        return out
    }

    fun bodyRotation(): KeyValue {
        val rotateAngle = this.rotationAngle()
        return KeyValue(body.shape.rotateProperty(), body.shape.rotate + rotateAngle.degrees())
    }

    fun moveBodyParts(): Set<KeyValue> {
        val out: MutableSet<KeyValue> = mutableSetOf()
        val transform = { bp: BodyPart ->
            val oldBPOffset = bp.centerOffset.copy()
            val rotAngle = this.rotationAngle()
            bp.rotateAroundCenter(rotAngle.rad)
            // bp center offset is already angle updated
            val newX = bp.getX() - oldBPOffset.x + bp.centerOffset.x + this.speed.x
            val newY = bp.getY() - oldBPOffset.y + bp.centerOffset.y + this.speed.y
            out.add(KeyValue(bp.getXProperty(), newX))
            out.add(KeyValue(bp.getYProperty(), newY))
        }
        this.sensors.forEach {
            transform(it)
        }
        this.motors.forEach {
            transform(it)
        }
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
            speedY: Double,
            brainSize: Int = 5,
            brain: Network? = null
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
                polarity = 1
            )
            val motorRight = Motor(
                centerOffset = DoubleVector(longSide / 2, -sensorsDistance / 2),
                bodyCenter = bodyCenter
            )
            val motorLeft = Motor(
                centerOffset = DoubleVector(longSide / 2, sensorsDistance / 2),
                bodyCenter = bodyCenter
            )
            var br = brain
            if (br == null)
                br = Network.generateRandomOfSize(brainSize)

            return Vehicle(
                body,
                arrayOf(motorLeft, motorRight),
                arrayOf(sensorLeft, sensorRight),
                DoubleVector(speedX, speedY),
                br
            )
        }

        fun randomSimpleVehicle(
            worldWidth: Double, worldHeight: Double,
            vehicleLength: Double,
            vehicleHeight: Double,
            sensorsDistance: Double,
            brainSize: Int = 5,
            brain: Network? = null
        ): Vehicle {
            return Vehicle.Factory.simpleVehicle(
                // DEBUG OVERRIDE
                Random.nextDouble(100.0, worldWidth - 100.0),
                Random.nextDouble(100.0, worldHeight - 100.0),
                vehicleHeight, vehicleLength, 1.0, sensorsDistance,
                Random.nextDouble(-10.0, 10.0),
                Random.nextDouble(-10.0, 10.0),
                brainSize,
                brain
            )

        }
    }
}

