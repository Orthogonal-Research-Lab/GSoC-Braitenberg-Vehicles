package agent

import Dot
import DoubleVector
import FITNESS_TICKS_MEMORY_LEN
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
import javafx.animation.Interpolator
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
        get() = speedsArchive[0]

    var speedLastTurn: DoubleVector = DoubleVector(0.0, 0.0)
        get() = speedsArchive[1]

    private var speedsArchive = LinkedList<DoubleVector>()

    init{
        speedsArchive.addFirst(speed)
    }

    private fun getAngle() = angleToXAxis(Dot(this.speed.x, this.speed.y))
    private fun getLayoutX() = this.body.shape.layoutX
    private fun getLayoutY() = this.body.shape.layoutY
    private fun getX() = (this.body.shape as Rectangle).x + this.getLayoutX()
    private fun getY() = (this.body.shape as Rectangle).y + this.getLayoutY()


    /**
     * Changes velocity and dAngle of current vehicle, based on sensors affected by objects in the world.
     * Return value is whether transgression of walls occurs in this tick.
     */
    private fun updateSpeedsArchive(affectors: Collection<WorldObject>):Boolean {
        if (this.speedsArchive.size >= FITNESS_TICKS_MEMORY_LEN) { //deque filled, make space on the end
            this.speedsArchive.removeLast()
        }
        val (newSpeed, transgressesWalls) = this.perceptEffects(affectors)
        this.speedsArchive.addFirst(newSpeed)
        return transgressesWalls
    }


    private fun perceptEffects(affectors: Collection<WorldObject>): Pair<DoubleVector, Boolean> {
        val sensorInput = this.sensors.map { it.percept(affectors) }
        val motorOutput = this.brain.propagate(sensorInput.toTypedArray())
        val pureSpeed = motorOutput.sum()
        val (adjustedSpeed,transgress) = transgressWalls(pureSpeed)
        return Pair(adjustedSpeed, transgress)
    }

    /**
     * Returns speed of vehicle to "portal" through a wall to the opposite site, and whether such portalling has occurred
     */
    private fun transgressWalls(speed: DoubleVector): Pair<DoubleVector, Boolean> {
        val (fromLeft, fromUp) = arrayOf(abs(this.getX()), abs(this.getY()))
        if (fromLeft == 0.0 || fromUp == 0.0) return Pair(speed,false) //just initialized
        val (fromRight, fromDown) = arrayOf(abs(model.worldEnd.x - fromLeft), abs(model.worldEnd.y - fromUp))
        var transgress = false
        val out = speed.copy()
        if (fromLeft + speed.x > model.worldEnd.x) {
            val Xspeedleftover = speed.x - fromRight
            out.x = -fromLeft + Xspeedleftover
            transgress = true
        } else if (fromLeft + speed.x < 0) { //speedx is negative!
            val Yspeedleftover = fromLeft + speed.x
            out.x = fromRight + Yspeedleftover
            transgress = true
        }
        if (fromUp + speed.y > model.worldEnd.y) {
            val Xspeedleftover = speed.y - fromDown
            out.y = -fromUp + Xspeedleftover
            transgress = true
        } else if (fromUp + speed.y < 0) { //speedy is negative!
            val Yspeedleftover = fromUp + speed.y
            out.y = fromDown + Yspeedleftover
            transgress = true

        }
        return Pair(out, transgress)
    }

    /**
     * GA survival fitness of an individual vehicle
     */
    fun fitness(): Double {
        //Area under speeds archive polygon
        // TODO correct calculation for self-intersecting polygons
        val xCoords = mutableListOf<Double>()
        val yCoords = mutableListOf<Double>()
        var interXsum = 0.0
        var interYsum = 0.0
        val sa = this.speedsArchive
        for (i in 0 until sa.size) {
            interXsum += sa[i].x
            xCoords.add(interXsum)
            interYsum += sa[i].y
            yCoords.add(interYsum)
        }
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
        val transgress = this.updateSpeedsArchive(affectors)
        val animation = animationChanges(transgress)
        return animation
    }

    fun animationChanges(transgress: Boolean): Set<KeyValue> {
        val out: MutableSet<KeyValue> = mutableSetOf()
        val interpolator: Interpolator = if (transgress) Interpolator.DISCRETE else Interpolator.LINEAR
        out.add(this.bodyRotation(interpolator))
        out.addAll(this.moveBodyParts(interpolator))
        out.addAll(this.moveBody(interpolator))
        return out
    }

    private fun moveBody(interpolator: Interpolator): Collection<KeyValue> {
        val out: MutableSet<KeyValue> = mutableSetOf()
        out.add(KeyValue(body.shape.layoutXProperty(), this.getLayoutX() + this.speed.x, interpolator))
        out.add(KeyValue(body.shape.layoutYProperty(), this.getLayoutY() + this.speed.y, interpolator))
        return out
    }

    fun bodyRotation(interpolator: Interpolator): KeyValue {
        val rotateAngle = this.rotationAngle()
        return KeyValue(body.shape.rotateProperty(), body.shape.rotate + rotateAngle.degrees(), interpolator)
    }

    fun moveBodyParts(interpolator: Interpolator): Set<KeyValue> {
        val out: MutableSet<KeyValue> = mutableSetOf()
        val transform = { bp: BodyPart ->
            val oldBPOffset = bp.centerOffset.copy()
            val rotAngle = this.rotationAngle()
            bp.rotateAroundCenter(rotAngle.rad)
            // bp center offset is already angle updated
            val newX = bp.getX() - oldBPOffset.x + bp.centerOffset.x + this.speed.x
            val newY = bp.getY() - oldBPOffset.y + bp.centerOffset.y + this.speed.y
            out.add(KeyValue(bp.getXProperty(), newX, interpolator))
            out.add(KeyValue(bp.getYProperty(), newY, interpolator))
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

