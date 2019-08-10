package view

import org.knowm.xchart.*
import org.knowm.xchart.style.Styler
import round
import javax.swing.JFrame
import kotlin.math.absoluteValue
import kotlin.math.max

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
        var squareDim = 50*
                max(valuesX.map { it.absoluteValue }.max()!!, valuesY.map { it.absoluteValue }.max()!!).div(50)
        //dirty bug fixup
        if (squareDim > 1000) squareDim = 100.0
        chart.styler.xAxisMin = -squareDim
        chart.styler.xAxisMax = squareDim
        chart.styler.yAxisMin = -squareDim
        chart.styler.yAxisMax = squareDim
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
        var squareDim = 50*
                max(valuesX.map { it.absoluteValue }.max()!!, valuesY.map { it.absoluteValue }.max()!!).div(50)
        //dirty bug fixup
        if (squareDim > 1000) squareDim = 100.0
        chart.styler.xAxisMin = -squareDim
        chart.styler.xAxisMax = squareDim
        chart.styler.yAxisMin = -squareDim
        chart.styler.yAxisMax = squareDim
        plotPanel.repaint()
        lastUpdate = System.currentTimeMillis()
    }

}