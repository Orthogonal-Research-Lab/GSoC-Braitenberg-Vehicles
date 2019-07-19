package view

import data.SimInfo
import data.VehicleInfo
import javafx.scene.Node
import presenter.SimPresenter
import tornadofx.*

/**
 * Fragment containing various informations about the simulation current state.
 */
class InfoFragment<T>(infos: T, title: String? = "", icon: Node? = null) : Fragment(title, icon) {

    val presenter: SimPresenter by inject()

    override val root = vbox {

    }

    init {
        when (infos) {
            is SimInfo -> renderSimInfo(infos)
            is VehicleInfo -> renderVehicleInfo(infos)
            else -> Unit
        }
    }

    private fun renderVehicleInfo(infos: VehicleInfo) {
        with(root) {
            this += label(infos.toString())
        }
    }

    fun renderSimInfo(infos: SimInfo) {
        with(root) {
            this += label(infos.vehiclesCount.toString())
        }

    }

    fun vehicleSpeedDistrChart() {
    }

    override fun onUndock() {
        super.onUndock()
        presenter.renderReady()
    }
}