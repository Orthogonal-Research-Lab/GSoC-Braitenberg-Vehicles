package presenter

import agent.Vehicle
import config.SimConfigItem
import data.DataExporter
import data.SimInfo
import data.VehicleInfo
import javafx.animation.Timeline
import javafx.event.EventHandler
import model.SimModel
import model.WorldObject
import tornadofx.*
import view.*
import kotlin.math.ceil

class SimPresenter() : Controller() {
    lateinit var conf: SimConfigItem
    lateinit var view: SimView
    lateinit var model: SimModel

    fun epochCountProperty() = model.epochCountProperty()

    val configView: WelcomeScreen by inject()
    var running = true
    var paused = false
    var interval: Int = 0

    private var gaUpdateQueued = false

    init {
        subscribe<RenderReadyEvent> {
            if (running and !paused) {
                DataExporter.exportTo("datatrace", model)
                model.tick++
                updateRender()
            }
        }
    }

    /**
     * Create world, starting vehicles & launch the rendering process.
     */
    fun startSimulation(conf: SimConfigItem) {
        this.conf = conf
        interval = ceil(1000F / conf.fps.value.toDouble()).toInt()
        model =
            SimModel.instance(
                conf.worldWidth.value.toDouble(),
                conf.worldHeight.value.toDouble(),
                effectMin = conf.minObjectEffect.value.toDouble(),
                effectMax = conf.maxObjectEffect.value.toDouble(),
                worldObjectCount = conf.objectCount.value.toInt(),
                startingVehicles = conf.startingAgents.value.toInt(),
                vehicleLength = conf.vehicleLength.value.toDouble(),
                vehicleHeight = conf.vehicleWidth.value.toDouble(),
                sensorsDistance = conf.sensorsDistance.value.toDouble(),
                brainSize = conf.brainSize.value.toInt(),
                rateLuckySelected = conf.rateLuckySelected.value.toDouble(),
                rateEliteSelected = conf.rateEliteSelected.value.toDouble(),
                matingRate = conf.matingRate.value.toDouble(),
                mutationRate = conf.mutationRate.value.toDouble(),
                presenter = this
            )
        view = find(view.SimView::class)
        configView.replaceWith(SimView::class)
        view.drawWorldBoundaries(conf.worldWidth.value.toDouble(), conf.worldHeight.value.toDouble())
        fire(UpdateRenderEvent()) //tells view to render model
    }

    /**
     * Updates render frame
     */
    fun updateRender() {
        val timeline = thisTickAnimation()
        timeline.onFinished = EventHandler {
            if (gaUpdateQueued) {
                model.nextEpoch()
                gaUpdateQueued = false
                fire(UpdateRenderEvent()) //update GUI with new vehicles
            } else fire(RenderReadyEvent())
        }
        timeline.play()

    }

    fun thisTickAnimation(): Timeline {
        val vehicles = getCurrentVehicles()
        return timeline {
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
    }

    fun getCurrentVehicles(): Collection<Vehicle> {
        return model.vehicles
    }

    fun getCurrentWorldObjects(): MutableSet<WorldObject> {
        return model.objects
    }

    /**
     * Queues genetic algorithm update and waits until it finishes. Returns outselected vehicles.
     */
    fun queueEpochUpdate() {
        pause()
        gaUpdateQueued = true
    }

    fun pause() {
        paused = true
    }

    fun unpause() {
        paused = false
    }

    fun renderReady() {
        paused = false
        fire(RenderReadyEvent())
    }

    fun openSimInfoFragment() {
        pause()
        val infos = SimInfo(this.model.vehicles.size, this.model.vehicles.map {it.speed})
        val fragment = InfoFragment(infos)
        fragment.openModal(block = true)
    }

    fun showVehicleInformation(vehicle: Vehicle) {
        pause()
        val infos = VehicleInfo(vehicle)
        val fragment = InfoFragment(infos)
        fragment.openModal(block = true)
    }

}