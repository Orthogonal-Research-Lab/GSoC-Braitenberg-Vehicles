import javafx.application.Application
import tornadofx.*
import view.WelcomeScreen

class SimApp : App(WelcomeScreen::class, Styles::class)

fun main(args: Array<String>) {
    Application.launch(SimApp::class.java, *args)
}

class Styles : Stylesheet() {
    init {
        root {
            minWidth = 600.px
            minHeight = 600.px
        }
    }
}
