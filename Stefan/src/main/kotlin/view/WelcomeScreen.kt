package view

import SimConfigItem
import presenter.SimPresenter
import tornadofx.*

class WelcomeScreen : View("Braitenberg vehicles simulation") {
    val presenter: SimPresenter by inject()
    val simconf: SimConfigItem by inject()

    override val root = vbox {
        label("Welcome to tHe simulation! Choose starting paramters")
        form {
            field("Number of starting vehicles") {
                textfield(simconf.startingAgents) {
                    filterInput { it.controlNewText.isInt() }
                }
            }
            fieldset("World size") {
                field("World length") {
                    textfield(simconf.worldlength) {

                    }
                }
            }
            button {
                text("Start simulation")
                action {
                    //presenter.startSimulation(simconf.item)
                }
                enableWhen(simconf.valid)
            }
        }
    }
}
