import javafx.application.Application
import tornadofx.*
import view.SimView

class SimApp : App(SimView::class, Styles::class)

fun main(args: Array<String>) {
    Application.launch(SimApp::class.java, *args)
}

class Styles : Stylesheet() {
    init {
        root {
            minWidth = 400.px
            minHeight = 200.px
        }
    }
}
