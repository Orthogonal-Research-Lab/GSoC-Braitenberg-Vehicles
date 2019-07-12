package model

import DoubleVector
import agent.Vehicle
import agent.brain.Network
import org.nield.kotlinstatistics.WeightedCoin
import presenter.SimPresenter
import kotlin.random.Random
import kotlin.reflect.KProperty

/**
 * Contains buisness logic of the world. Genetic algorithm part of the model is one-point crossover, elitarism and luck survival.
 */
class SimModel(
    val worldWidth: Double,
    val worldHeight: Double,
    val objects: MutableSet<WorldObject>,
    var vehicles: MutableList<Vehicle>,
    mutationRate: Double,
    matingRate: Double, /* how probable is it, that unique vehicle pair mates? */
    val rateEliteSelected: Double,
    rateLuckySelected: Double,
    val presenter: SimPresenter
) {
    val worldEnd = DoubleVector(worldWidth, worldHeight)
    val mutationCoin = WeightedCoin(mutationRate)
    val matingCoin = WeightedCoin(matingRate)
    val selectLuckyCoin = WeightedCoin(rateLuckySelected)
    var epochCount = 0

    /**
     * Returns vehicles active after an epoch update.
     */
    fun nextEpoch(): Collection<Vehicle> {
        this.mutateBrains()
        this.crossoverBrains()
        this.selectVehicles()
        ++epochCount
        return this.vehicles
    }

    private fun mutateBrains() {
        vehicles.forEach {
            val binBrain = it.brain.toBinary()
            for (i in 0 until binBrain.length()) {
                if (mutationCoin.flip()) binBrain.flip(i)
            }
            it.brain = Network.fromBinary(binBrain)
        }
    }

    private fun crossoverBrains() {
        //order vehicles
        val arr = vehicles.toTypedArray()
        for (m1 in 0 until arr.size) {
            for (m2 in m1 until arr.size) {
                if (matingCoin.flip()) {
                    vehicles.add(mateBrains(arr[m1].brain, arr[m2].brain))
                }
            }
        }
    }

    private fun mateBrains(brain1: Network, brain2: Network): Vehicle {
        val b1 = brain1.toBinary()
        val b2 = brain2.toBinary()
        val coPoint = Random.nextInt(b1.length())
        val childBrain = b1.crossover(b2, coPoint)
        return Vehicle.randomSimpleVehicle(
            worldHeight = worldHeight,
            worldWidth = worldWidth,
            brain = Network.fromBinary(childBrain),
            sensorsDistance = presenter.conf.sensorsDistance.value.toDouble(),
            vehicleHeight = presenter.conf.vehicleWidth.value.toDouble(),
            vehicleLength = presenter.conf.vehicleLength.value.toDouble()
        )
    }

    private fun selectVehicles() {
        val fitnessDecrVehicles =
            fitness(this.vehicles).toList().sortedBy { (_, v) -> v }.reversed().toMap().keys.toList()
        var died = mutableSetOf<Vehicle>()
        for (i in 0 until fitnessDecrVehicles.size) {
            if (i > fitnessDecrVehicles.size * rateEliteSelected && !selectLuckyCoin.flip()) died.add(
                fitnessDecrVehicles[i]
            )
        }
        this.vehicles.removeAll(died)
    }

    private fun fitness(individuals: Collection<Vehicle>): HashMap<Vehicle, Double> {
        val out = hashMapOf<Vehicle, Double>()
        individuals.forEach {
            out.put(it, it.speed.vecLength() + it.oldSpeed.vecLength())
        }
        return out
    }


    companion object Factory {
        var singleton: SimModel? = null
        /**
         * All vehicles default, have same length, default world objects.
         */
        fun instance(
            worldWidth: Double,
            worldHeight: Double,
            startingVehicles: Int,
            vehicleLength: Double,
            vehicleHeight: Double,
            worldObjectCount: Int,
            sensorsDistance: Double,
            effectMin: Double,
            effectMax: Double,
            brainSize: Int,
            mutationRate: Double,
            matingRate: Double, /* how probable is it, that unique vehicle pair mates? */
            rateEliteSelected: Double,
            rateLuckySelected: Double,
            presenter: SimPresenter
        ): SimModel {
            if (singleton != null) return singleton!!
            val vehicles: MutableList<Vehicle> = mutableListOf()
            for (i in 1..startingVehicles) {
                vehicles.add(
                    Vehicle.randomSimpleVehicle(
                        worldWidth,
                        worldHeight,
                        vehicleLength,
                        vehicleHeight,
                        sensorsDistance,
                        brainSize
                    )
                )

            }

            val startWorldObjects: MutableSet<WorldObject> = mutableSetOf()
            val objectSize = 10.0
            for (i in 1..worldObjectCount) startWorldObjects.add(
                WorldObject.randomWorldObject(
                    worldWidth,
                    worldHeight,
                    effectMin,
                    effectMax,
                    objectSize
                )
            )
            singleton = SimModel(
                worldWidth,
                worldHeight,
                startWorldObjects,
                vehicles,
                mutationRate,
                matingRate,
                rateEliteSelected = rateEliteSelected,
                rateLuckySelected = rateLuckySelected,
                presenter = presenter
            )
            return singleton!!
        }

        operator fun getValue(vehicle: Vehicle, property: KProperty<*>): SimModel {
            return singleton!!
        }
    }
}

