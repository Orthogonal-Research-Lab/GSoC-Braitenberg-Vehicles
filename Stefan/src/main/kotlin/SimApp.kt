
import javafx.application.Application
import tornadofx.*
import view.WelcomeScreen
import java.awt.Window

class SimApp : App(WelcomeScreen::class, Styles::class) {

    override fun stop() {
        super.stop()
        Window.getWindows().forEach { it.dispose() } //close all additional rendering (e.g. plot) frames
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
            minWidth = 800.px
            minHeight = 800.px
        }

        Companion.infobutton {
            padding = box(0.px)
            spacing = 0.px
        }

    }
}
