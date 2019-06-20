package presenter

import model.SimModel
import tornadofx.*
import view.SimView
import kotlin.math.ceil

class SimPresenter() : Controller() {
    lateinit var model: SimModel
    lateinit var view: SimView
    var running = true
    var paused = false
    var interval: Int = 0

    init {
        subscribe<RenderReadyEvent> {
            if (running and !paused)
                updateRender()
        }
    }

    /**
     * Create world, starting vehicles & launch the rendering process.
     */
    fun startSimulation(
        worldWidth: Double,
        worldHeight: Double,
        vehiclesCount: Double,
        view: SimView,
        frameRate: Byte
    ) {
        this.view = view
        interval = ceil(1000F / frameRate).toInt()
        model =
            SimModel.Factory.defaultModel(
                600.0, 400.0, vehicleHeight = 20.0, vehicleLength = 40.0, effectMin = 10.0,
                effectMax = 50.0, worldObjectCount = 10
            )
        this.view.renderWorld(model)
        updateRender()
    }

    /**
     * Updates render frame
     */
    fun updateRender() {
        if (running) {
            val vehicles = model.vehicles
            val timeline = timeline {
                keyframe(interval.millis) {
                    vehicles.forEach {
                        it.calcCurrentUpdate(model.objects).forEach { kv ->
                            run {
                                this += kv
                            }
                        }
                    }
                }
            }
            timeline.setOnFinished { fire(RenderReadyEvent()) }
            timeline.play()
        }
    }

}