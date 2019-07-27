package view

import EpochInfoConverter
import Styles
import agent.Vehicle
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.shape.Line
import model.WorldObject
import path
import presenter.SimPresenter
import tornadofx.*
import kotlin.system.exitProcess

class SimView : View() {
    private val presenter = find(SimPresenter::class)
    private val canvas: AnchorPane
    private val speedsHist: RealTimeHistogram

    override val root = vbox {
        anchorpane {
            label(presenter.epochCountProperty(), converter = EpochInfoConverter())
        }
        keyboard {
            addEventFilter(KeyEvent.KEY_PRESSED) { e ->
                processInput(e.code)
            }
        }
    }

    init {
        canvas = root.children.filtered { it is AnchorPane }[0] as AnchorPane
        speedsHist = RealTimeHistogram(presenter.getCurrentVehicles().map { it.speed.x }, presenter, "Current vehicle x-axis speeds", bins = 5)
        subscribe<UpdateRenderEvent> {
            if (!canvas.getChildList()!!.any { it is WorldObjectGroup }) renderWorldObjects(presenter.getCurrentWorldObjects())
            renderVehicles(presenter.getCurrentVehicles())

            presenter.renderReady()
        }
        var speeds: List<Double>
        subscribe<RenderReadyEvent> {
            speeds = presenter.getCurrentVehicles().map { it.speed.x }
            speedsHist.updateData(speeds)

        }
    }

    fun drawWorldBoundaries(worldWidth: Double, worldHeight: Double) {
        with(canvas) {
            //world boundaries
            this += Line(worldWidth, 0.0, worldWidth, worldHeight)
            this += Line(0.0, worldHeight, worldWidth, worldHeight)
            this += infobutton(worldWidth)
        }
    }

    /**
     * Fetches the infobutton with immage. World width is used to change its square measurement.
     */
    private fun infobutton(worldWidth: Double): Button {
        val resF = path(
            "file:///",
            "C:",
            "src",
            "Braitenberg-Vehicles",
            "src",
            "main",
            "res",
            "drawable",
            "info_button.png"
        )
        val infoicon = ImageView(resF)
        infoicon.fitWidth = worldWidth / 20.0
        infoicon.fitHeight = infoicon.fitWidth
        return button("", infoicon) {
            addClass(Styles.infobutton)
            root.requestFocus()
            this.layoutX = worldWidth - infoicon.fitWidth
            this.layoutY = 0.0

            action {
                presenter.openSimInfoFragment()
            }
        }
    }


    private fun processInput(code: KeyCode) {
        when (code) {
            KeyCode.ESCAPE -> {
                presenter.running = false
                exitProcess(0)
            }
            KeyCode.SPACE -> {
                if (presenter.paused) {
                    presenter.renderReady()
                } else {
                    presenter.pause()
                }
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