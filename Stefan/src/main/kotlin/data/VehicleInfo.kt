package data

import agent.Vehicle

@ExperimentalUnsignedTypes
class VehicleInfo(vehicle: Vehicle) {
    val oldSpeed = vehicle.speedLastTurn
    val newSpeed = vehicle.speed
    val vehicleBrainMtx = vehicle.brain.adjMatrix

    override fun toString() = "Current speed: $newSpeed \nSpeed in previous tick: $oldSpeed\n Vehicle brain adjacency matrix: $vehicleBrainMtx"
}