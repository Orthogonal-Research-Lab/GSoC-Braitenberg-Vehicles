package view

import config.SimConfigItem
import javafx.scene.control.TextField
import presenter.SimPresenter
import tornadofx.*

/**
 * Defines the simulation configuration form, including layout and
 */
class WelcomeScreen : View("Welcome to the GA Braitenberg vehicles simulation!") {

    val model: SimConfigItem by inject()
    val presenter: SimPresenter by inject()


    override val root = scrollpane {
        label("Welcome! Please, specify starting parameters:")
        form {
            hbox {
                fieldset("General settings") {
                    field("Number of starting vehicles") {
                        spinner(
                            min = 1,
                            max = 1000,
                            amountToStepBy = 1,
                            editable = true,
                            property = model.startingAgents
                        )
                    }
                    field("Frame rate (fps)") {
                        spinner(
                            min = 1,
                            max = 36,
                            amountToStepBy = 1,
                            editable = true,
                            property = model.fps
                        )
                    }
                }
                fieldset("World parameters:") {
                    field("World length") {
                        spinner(
                            min = 250.0,
                            max = 10000.0,
                            amountToStepBy = 10,
                            editable = true,
                            property = model.worldHeight
                        )
                    }
                    field("World width") {
                        spinner(
                            min = 250.0,
                            max = 10000.0,
                            amountToStepBy = 10,
                            editable = true,
                            property = model.worldHeight
                        )
                    }
                }

            }

            hbox {
                fieldset("World objects") {
                    field("Object count") {
                        spinner(
                            min = 1,
                            max = 100,
                            amountToStepBy = 1,
                            editable = true,
                            property = model.objectCount
                        )
                    }
                    field("Object size") {
                        spinner(
                            min = 0.1,
                            max = (model.worldHeight.value as Double / 20.0),
                            amountToStepBy = 0.1,
                            editable = true,
                            property = model.objectSize
                        )
                    }
                    field("Minimum effect") {
                        spinner(
                            min = 1.0,
                            amountToStepBy = 1.0,
                            editable = true,
                            property = model.minObjectEffect
                        )
                    }
                    field("Maximum effect") {
                        spinner(
                            min = model.minObjectEffect.value.toDouble() + 1.0,
                            amountToStepBy = 1.0,
                            editable = true,
                            property = model.maxObjectEffect
                        )
                    }
                    field("Object placement") {
                        togglegroup {
                            radiobutton("randomized", value = true) {
                            }
                            this.selectedValueProperty<Boolean>().value = true // check button
                        }
                    }
                }

                fieldset("Vehicles") {
                    field("Length (long side)") {
                        spinner(
                            min = 1.0,
                            max = (model.worldHeight.value as Double / 5.0),
                            amountToStepBy = 0.1,
                            editable = true,
                            property = model.vehicleLength
                        )
                    }
                    field("Width (short side)") {
                        spinner(
                            min = 0.5,
                            max = (model.worldHeight.value as Double / 10.0),
                            amountToStepBy = 0.1,
                            editable = true,
                            property = model.vehicleWidth
                        )
                    }
                    field("Sensors/motors distance") {
                        spinner(
                            amountToStepBy = 1.0,
                            editable = true,
                            property = model.sensorsDistance
                        )
                    }
                    field("Vehicle placement") {
                        togglegroup {
                            radiobutton("randomized", value = true)
                            this.selectedValueProperty<Boolean>().value = true // check button
                        }
                    }
                    field("Brain nodes size") {
                        spinner(
                            min = 1.0,
                            max = 100.0,
                            amountToStepBy = 1,
                            editable = true,
                            property = model.brainSize
                        )
                    }
                    field("Network architecture") {
                        togglegroup {
                            radiobutton("randomized", value = true)
                            this.selectedValueProperty<Boolean>().value = true // check button
                        }
                    }
                }
            }
            fieldset("Genetic algorithm settings") {
                field("Chance of mutation happening in a taken gene") {
                    spinner(
                        min = 0.0,
                        max = 1.0,
                        amountToStepBy = 0.01,
                        editable = true,
                        property = model.mutationRate
                    )
                }
                field("Expected fraction of pairs that will mate, across all possible distinct pairs") {
                    spinner(
                        min = 0.0,
                        max = 1.0,
                        amountToStepBy = 0.01,
                        editable = true,
                        property = model.matingRate
                    )
                }
                field("Expected fraction of the ones selected by fitness") {
                    spinner(
                        min = 0.0,
                        max = 1.0,
                        amountToStepBy = 0.01,
                        editable = true,
                        property = model.rateEliteSelected
                    )
                }

                field("Expected fraction of the ones selected by chance") {
                    spinner(
                        min = 0.0,
                        max = 1.0,
                        amountToStepBy = 0.01,
                        editable = true,
                        property = model.rateLuckySelected
                    )
                }
            }

            checkbox("Save this settings as default", model.remember).action {
                // Save the state every time its value is changed
                with(model.config) {
                    set(model.KEY_REMEMBER to model.remember.value)
                    save()
                }
            }

            button {
                text("Start simulation")
                action {
                    model.commit()
                    presenter.startSimulation(model)
                }
                enableWhen(model.valid)
            }
        }
    }

    fun makeNumericOnly(textField: TextField) {
        textField.textProperty().addListener { _, _, newValue ->
            if (!newValue.matches("\\d*".toRegex())) {
                textField.setText(newValue.replace("[^\\d]".toRegex(), ""))
            }
        }
    }

}
