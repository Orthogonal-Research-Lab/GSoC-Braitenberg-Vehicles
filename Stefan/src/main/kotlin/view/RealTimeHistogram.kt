package view

import org.knowm.xchart.style.Styler
import org.knowm.xchart.*
import org.nield.kotlinstatistics.randomFirstOrNull
import presenter.SimPresenter
import round
import javax.swing.JFrame


/**
 * //TODO presenter
 */
class RealTimeHistogram(values: Collection<Double>, private val presenter: SimPresenter, title: String = "Plot",
                        xAxisTitle: String = "Value", yAxisTitle: String = "Frequency",
                        private val bins: Int = 120, width: Int = 800, height: Int = 600
                        ) {

    private val plotPanel: XChartPanel<CategoryChart>
    private val chart: CategoryChart = CategoryChartBuilder()
        .width(800).height(600)
        .title(title)
        .xAxisTitle(xAxisTitle)
        .yAxisTitle(yAxisTitle)
        .theme(Styler.ChartTheme.GGPlot2).build()

    init {
        val hist = Histogram(values, bins, values.min()?:0.0, values.max()?:0.0)
        chart.addSeries("values", hist.getxAxisData().map { it.round(2) }, hist.getyAxisData())
        plotPanel = XChartPanel(chart)


        // Create and set up the window.
        val frame = JFrame("XChart")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.add(plotPanel)

        // Display the window.
        frame.pack()
        frame.isVisible = true
    }

    fun updateData(values: Collection<Double>) {
        if (values.isEmpty()) return
        if (values.any { it < -100000 }) println("lol programin suckz")
        val hist = Histogram(values, bins, values.min()!!, values.max()!!)
        chart.updateCategorySeries("values", hist.getxAxisData().map { it.round(2) }, hist.getyAxisData(), null)
        plotPanel.revalidate()
        plotPanel.repaint()
    }

}