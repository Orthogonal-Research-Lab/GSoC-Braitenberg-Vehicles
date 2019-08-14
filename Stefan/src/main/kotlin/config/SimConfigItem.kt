package config

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*


/**
 * Configuration model.
 */
data class SimConfig(
    var startingAgents: Int = 0, var fps: Int = 0,
    var rateLuckySelected: Double = 0.0,
    var rateEliteSelected: Double = 0.0,
    var matingRate: Double = 0.0,
    var mutationRate: Double = 0.0,
    var brainSize: Int = 0,
    var worldLength: Double = 0.0,
    var worldWidth: Double = 0.0,
    var objectSize: Double = 0.0,
    var objectCount: Int = 0,

    var minObjectEffect: Double = 0.0,

    var maxObjectEffect: Double = 0.0,

    var vehicleLength: Double = 0.0,
    var vehicleWidth: Double = 0.0,

    var sensorsDistance: Double = 0.0,
    var remember: Boolean = false,
    var dataTraceDir: String = ""
)


class SimConfigItem : ItemViewModel<SimConfig>(SimConfig()) {
    private val KEY_ELITE_RATE = "eliteRate"
    private val KEY_LUCKY_RATE = "luckyRate"
    private val KEY_MUTRATE = "mutRate"
    private val KEY_MATING_RATE = "matRate"
    private val KEY_BRAINSIZE = "brainSize"
    val KEY_DEFAULT_FPS = "defaultFPS"
    val KEY_DEFAULT_STARTINGVEHICLES = "defaultStartingVehicles"
    val KEY_WORLDHEIGHT = "worldLength"
    val KEY_WORLDWIDTH = "worldHeight"
    val KEY_OBJCOUNT = "objCount"
    val KEY_OBJSIZE = "objSize"
    val KEY_MINEFFECT = "minEffect"
    val KEY_MAXEFFECT = "maxEffect"
    val KEY_VEHWIDTH = "vehWidth"
    val KEY_VEHLENGTH = "vehLength"
    val KEY_SENSDIST = "sensorsDistance"
    val KEY_DATATRACEDIR = "dataTraceDir"

    val KEY_REMEMBER = "rememberDefaultSettings"

    val startingAgents =
        bind {
            SimpleIntegerProperty(
                item?.startingAgents,
                "",
                config.string(KEY_DEFAULT_STARTINGVEHICLES, "10").toInt()
            )
        }
    val fps = bind {
        SimpleIntegerProperty(
            item?.fps,
            "",
            config.string(KEY_DEFAULT_STARTINGVEHICLES, "10").toInt()
        )
    }

    val worldHeight =
        bind { SimpleDoubleProperty(item?.worldLength, "", config.string(KEY_WORLDHEIGHT, "800.0").toDouble()) }
    val worldWidth =
        bind { SimpleDoubleProperty(item?.worldWidth, "", config.string(KEY_WORLDWIDTH, "800.0").toDouble()) }
    val objectCount = bind { SimpleIntegerProperty(item?.objectCount, "", config.string(KEY_OBJCOUNT, "10").toInt()) }
    val objectSize = bind {
        SimpleDoubleProperty(
            item?.objectSize,
            "",
            config.string(KEY_WORLDWIDTH, "10.0").toDouble()
        )
    }
    val minObjectEffect = bind {
        SimpleDoubleProperty(item?.minObjectEffect, "", config.string(KEY_MINEFFECT, "10.0").toDouble())
    }
    val maxObjectEffect =
        bind { SimpleDoubleProperty(item?.maxObjectEffect, "", config.string(KEY_MAXEFFECT, "50.0").toDouble()) }
    val vehicleWidth =
        bind { SimpleDoubleProperty(item?.vehicleWidth, "", config.string(KEY_VEHWIDTH, "20.0").toDouble()) }
    val vehicleLength =
        bind { SimpleDoubleProperty(item?.vehicleLength, "", config.string(KEY_VEHLENGTH, "40.0").toDouble()) }
    val sensorsDistance =
        bind { SimpleDoubleProperty(item?.sensorsDistance, "", config.string(KEY_SENSDIST, "10.0").toDouble()) }
    val brainSize =
        bind { SimpleDoubleProperty(item?.brainSize, "", config.string(KEY_BRAINSIZE, "5.0").toDouble()) }
    val mutationRate =
        bind { SimpleDoubleProperty(item?.mutationRate, "", config.string(KEY_MUTRATE, "0.05").toDouble()) }
    val matingRate =
        bind { SimpleDoubleProperty(item?.matingRate, "", config.string(KEY_MATING_RATE, "0.05").toDouble()) }
    val rateEliteSelected =
        bind { SimpleDoubleProperty(item?.rateEliteSelected, "", config.string(KEY_ELITE_RATE, "0.1").toDouble()) }
    val rateLuckySelected =
        bind { SimpleDoubleProperty(item?.rateLuckySelected, "", config.string(KEY_LUCKY_RATE, "0.05").toDouble()) }
    val remember = bind { SimpleBooleanProperty(item.remember, "", false) }
    val dataTraceDir = bind { SimpleStringProperty(item.dataTraceDir, "", "datatrace") }

    override fun onCommit() {
        if (remember.value) {
            with(config) {
                set(KEY_BRAINSIZE to brainSize.value)
                set(KEY_DEFAULT_FPS to fps.value)
                set(KEY_DEFAULT_STARTINGVEHICLES to startingAgents.value)
                set(KEY_ELITE_RATE to rateEliteSelected.value)
                set(KEY_LUCKY_RATE to rateLuckySelected.value)
                set(KEY_MATING_RATE to matingRate.value)
                set(KEY_MUTRATE to mutationRate.value)
                set(KEY_MINEFFECT to minObjectEffect.value)
                set(KEY_MAXEFFECT to maxObjectEffect.value)
                set(KEY_OBJCOUNT to objectCount.value)
                set(KEY_OBJSIZE to objectSize.value)
                set(KEY_SENSDIST to sensorsDistance.value)
                set(KEY_VEHLENGTH to vehicleLength.value)
                set(KEY_VEHWIDTH to vehicleWidth.value)
                set(KEY_WORLDHEIGHT to worldHeight.value)
                set(KEY_WORLDWIDTH to worldWidth.value)
                save()
            }
        }
        updateItem()
    }

    private fun updateItem() {
        this.item.brainSize = brainSize.value.toInt()
        this.item.fps = fps.value.toInt()
        this.item.matingRate = matingRate.value.toDouble()
        this.item.maxObjectEffect = maxObjectEffect.value.toDouble()
        this.item.minObjectEffect = minObjectEffect.value.toDouble()
        this.item.mutationRate = mutationRate.value.toDouble()
        this.item.objectCount = objectCount.value.toInt()
        this.item.rateEliteSelected = rateEliteSelected.value.toDouble()
        this.item.rateLuckySelected = rateLuckySelected.value.toDouble()
        this.item.sensorsDistance = sensorsDistance.value.toDouble()
        this.item.vehicleLength = vehicleLength.value.toDouble()
        this.item.startingAgents = startingAgents.value.toInt()
        this.item.vehicleWidth = vehicleWidth.value.toDouble()
        this.item.worldLength = worldWidth.value.toDouble()
        this.item.worldLength = worldHeight.value.toDouble()
    }
}