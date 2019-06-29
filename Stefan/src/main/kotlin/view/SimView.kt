package view

import agent.Vehicle
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.AnchorPane
import model.SimModel
import model.WorldObject
import presenter.RenderReadyEvent
import presenter.SimPresenter
import tornadofx.*

class SimView : View() {
    val presenter: SimPresenter = SimPresenter()
    val canvas: AnchorPane
    val frameRate = 1

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
                    in arrayOf(KeyCode.RIGHT, KeyCode.KP_RIGHT) -> {
                        presenter.nextEpoch()
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
    }

    fun renderWorld(model: SimModel) {
        renderVehicles(model.vehicles)
        renderWorldObjects(model.objects)
    }


    /**
     * Adds vehicles shapes to the simulation.
     */
    fun renderVehicles(
        vehicles: Collection<Vehicle>
    ) { //TODO
        vehicles.forEach {
            with(canvas) {
                it.bodyParts.forEach { bp ->
                    this += bp.shape
                }
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