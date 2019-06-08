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

    fun startSimulation(
        worldWidth: Double,
        worldHeight: Double,
        vehiclesCount: Double,
        view: SimView,
        frameRate: Byte
    ) {
        this.view = view
        interval = ceil(1000F / frameRate).toInt()
        model = SimModel.Factory.defaultModel(800.0, 1000.0)
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
                        it.updateMovementVector(model.objects)
                        it.render.currentUpdate().forEach { kv ->
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