import javafx.beans.property.StringProperty
import tornadofx.*

class SimConfig {

    var name by property<String>()
    fun nameProperty() = getProperty(SimConfig::name)

    var startingAgents by property<String>()
    fun startingAgentsProperty() = getProperty(SimConfig::startingAgents)

    var worldLength by property<String>()
    fun worldLengthProperty() = getProperty(SimConfig::worldLength)

    var worldWidth by property<String>()
    fun worldWidthProperty() = getProperty(SimConfig::worldWidth)

    override fun toString() = name

    fun parse(): Array<Double> {
        val out = arrayOf(
            this.worldLength?.toDoubleOrNull() ?: 1000.0,
            this.worldWidth?.toDoubleOrNull() ?: 1000.0,
            this.startingAgents?.toDoubleOrNull() ?: 10.0
        )
        return out
    }
}

class SimConfigItem : ItemViewModel<SimConfig>(SimConfig()) {
    val name: StringProperty = bind { item?.nameProperty() }
    val startingAgents: StringProperty = bind { item?.startingAgentsProperty() }
    var worldlength: StringProperty = bind { item?.worldLengthProperty() }
    var worldwidth: StringProperty = bind { item?.worldWidthProperty() }
}