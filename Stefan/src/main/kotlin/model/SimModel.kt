package model

import Dot
import agent.Vehicle
import java.lang.Math.floor
import kotlin.random.Random

/**
 * Contains buisness logic of the world.
 */
class SimModel(
    val worldWidth: Double,
    val worldHeight: Double,
    val objects: MutableSet<WorldObject>,
    var vehicles: MutableSet<Vehicle>
) {
    companion object Factory {
        var worldEnd = Dot(0.0, 0.0)
        /**
         * All vehicles default, have same size, default world objects.
         */
        fun defaultModel(
            worldWidth: Double = 800.00,
            worldHeight: Double = 600.0,
            vehiclesCount: Int = 10,
            vehicleLength: Double = floor(worldWidth / 80),
            vehicleHeight: Double = floor(worldHeight / 150),
            worldObjectCount: Int = 5,
            sensorsDistance: Double = vehicleHeight / 2.0,
            effectMin: Double = 10.0,
            effectMax: Double = 100.0
        ): SimModel {
            worldEnd = Dot(worldWidth, worldHeight)
            val vehicles: MutableSet<Vehicle> = mutableSetOf()
            for (i in 1..vehiclesCount.toInt()) {
                vehicles.add(
                    Vehicle.Factory.simpleVehicle(
                        Random.nextDouble(0.0, worldWidth),
                        Random.nextDouble(0.0, worldHeight),
                        vehicleHeight, vehicleLength, 1.0, sensorsDistance,
                        Random.nextDouble(-10.0, 10.0),
                        Random.nextDouble(-10.0, 10.0)
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
            return SimModel(
                worldWidth,
                worldHeight,
                startWorldObjects,
                vehicles
            )
        }
    }
}

