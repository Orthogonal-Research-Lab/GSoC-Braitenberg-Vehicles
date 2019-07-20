package data

import agent.Vehicle

class VehicleInfo(vehicle: Vehicle) {
    val oldSpeed = vehicle.oldSpeed
    val newSpeed = vehicle.speed

    override fun toString() = "Current speed: $newSpeed \nSpeed in previous tick: $oldSpeed"
}