import data.DataExporter
import javafx.application.Application
import tornadofx.*
import view.WelcomeScreen

class SimApp : App(WelcomeScreen::class, Styles::class) {

    override fun stop() {
        super.stop()
    }
}

fun main(args: Array<String>) {
    Application.launch(SimApp::class.java, *args)
}

class Styles : Stylesheet() {

    companion object {
        val infobutton by cssclass()
    }

    init {
        root {
            minWidth = 600.px
            minHeight = 600.px
        }

        Companion.infobutton {
            padding = box(0.px)
            spacing = 0.px
        }

    }
}
