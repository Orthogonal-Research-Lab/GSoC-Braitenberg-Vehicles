package view

import org.knowm.xchart.*
import org.knowm.xchart.style.Styler
import round
import javax.swing.JFrame

class RealTimeScatter(valuesX: Collection<Double>, valuesY: Collection<Double>, title: String = "Plot",
                        xAxisTitle: String = "x-coordinate value", yAxisTitle: String = "y-coordinate value",
                        width: Int = 800, height: Int = 600
) {

    var lastUpdate = System.currentTimeMillis() - 1002
    private val plotPanel: XChartPanel<XYChart>
    private val chart: XYChart = XYChartBuilder()
        .width(width).height(height)
        .title(title)
        .xAxisTitle(xAxisTitle)
        .yAxisTitle(yAxisTitle)
        .theme(Styler.ChartTheme.GGPlot2).build()

    init {
        chart.styler.defaultSeriesRenderStyle = XYSeries.XYSeriesRenderStyle.Scatter
        chart.styler.isChartTitleVisible = false
        chart.styler.legendPosition = Styler.LegendPosition.InsideSW
        chart.styler.markerSize = 10
        chart.styler.xAxisMin = -100.0
        chart.styler.xAxisMax = 100.0
        chart.styler.yAxisMin = -100.0
        chart.styler.yAxisMax = 100.0
        chart.addSeries("values", valuesX.map { it.round(2) }, valuesY.map { it.round(2) })
        plotPanel = XChartPanel(chart)


        // Create and set up the window.
        val frame = JFrame("XChart")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.add(plotPanel)

        // Display the window.
        frame.pack()
        frame.isVisible = true
    }

    fun updateData(valuesX: Collection<Double>, valuesY: Collection<Double>) {
        if (valuesX.isEmpty() || valuesY.isEmpty()) return
        chart.updateXYSeries("values", valuesX.toList(),
            valuesY.toList(), null)
        plotPanel.repaint()
        lastUpdate = System.currentTimeMillis()
    }

}