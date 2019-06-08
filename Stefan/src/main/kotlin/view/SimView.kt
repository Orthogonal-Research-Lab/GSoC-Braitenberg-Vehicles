package view

import agent.Vehicle
import fac
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.AnchorPane
import model.SimModel
import presenter.RenderReadyEvent
import presenter.SimPresenter
import tornadofx.*
import world.WorldObject

class SimView : View() {
    val presenter: SimPresenter = SimPresenter()
    val canvas: AnchorPane
    val registeredVehiclesRender: MutableList<Vehicle.VehicleRender> = mutableListOf()
    val frameRate = 30

    override val root = vbox {
        anchorpane {}
        keyboard {
            addEventFilter(KeyEvent.KEY_PRESSED) { e ->
                when (e.code) {
                    KeyCode.ESCAPE -> presenter.running = false
                    KeyCode.SPACE -> {
                        presenter.paused = !presenter.paused
                        if (!presenter.paused) fire(RenderReadyEvent())
                    }
                    else -> Unit
                }
            }
        }
    }

    init {
        val (worldWidth, worldHeight, startingVehicles) = arrayOf(1000.0, 1000.0, 100.0)
        canvas = root.children.filtered { it is AnchorPane }[0] as AnchorPane
        runAsync {
            presenter.startSimulation(
                worldWidth,
                worldHeight,
                startingVehicles,
                find(SimView::class),
                frameRate.toByte()
            )
        } ui { }
        fac(5)
    }

    fun renderWorld(model: SimModel) {
        renderVehicles(model.vehicles)
        renderWorldObjects(model.objects)
    }


    /**
     * Adds vehicles shapes to the simulation.
     */
    fun renderVehicles(
        vehicles: Set<Vehicle>
    ) { //TODO
        vehicles.forEach {
            if (!registeredVehiclesRender.contains(it.render)) { //add new
                with(canvas) {
                    it.render.list.forEach { bp ->
                        this += bp
                    }
                }
                registeredVehiclesRender.add(it.render)
            }
        }
    }

    fun renderWorldObjects(wobjs: MutableCollection<WorldObject>) = wobjs.forEach {
        //TODO
        with(canvas) {
            this += it.shape
        }
    }
}