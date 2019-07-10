package view

import agent.Vehicle
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.shape.Line
import model.WorldObject
import presenter.SimPresenter
import presenter.UpdateRenderEvent
import tornadofx.*
import kotlin.system.exitProcess

class SimView : View() {
    val presenter: SimPresenter by inject()
    val canvas: AnchorPane

    override val root = vbox {
        anchorpane {}
        keyboard {
            addEventFilter(KeyEvent.KEY_PRESSED) { e ->
                processInput(e.code)
            }
        }
    }

    init {
        canvas = root.children.filtered { it is AnchorPane }[0] as AnchorPane
        subscribe<UpdateRenderEvent> {
            if (!canvas.getChildList()!!.any { it is WorldObjectGroup }) renderWorldObjects(presenter.getCurrentWorldObjects())
            renderVehicles(presenter.getCurrentVehicles())
            presenter.renderReady()
        }
    }

    fun drawWorldBoundaries(worldWidth: Double, worldHeight: Double) {
        with(canvas) {
            //world boundaries
            this += Line(worldWidth, 0.0, worldWidth, worldHeight)
            this += Line(0.0, worldHeight, worldWidth, worldHeight)
        }
    }


    fun processInput(code: KeyCode) {
        when (code) {
            KeyCode.ESCAPE -> {
                presenter.running = false
                exitProcess(0)
            }
            KeyCode.SPACE -> {
                if (presenter.paused) presenter.renderReady()
                else presenter.pause()
            }
            in arrayOf(KeyCode.RIGHT, KeyCode.KP_RIGHT) -> {
                runAsync {
                    presenter.queueEpochUpdate()
                } ui {}
            }
            else -> Unit
        }
    }

    /**
     * Adds vehicles shapes to the simulation.
     */
    fun renderVehicles(
        vehicles: Collection<Vehicle>
    ) {
        this.canvas.getChildList()?.removeIf { it is VehicleGroup }
        vehicles.forEach {
            with(canvas) {
                this += it.render
            }
        }
    }

    fun renderWorldObjects(wobjs: MutableCollection<WorldObject>) = wobjs.forEach {
        with(canvas) {
            this += it.shape
        }
    }
}