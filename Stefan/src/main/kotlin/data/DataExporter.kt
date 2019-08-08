package data

import com.opencsv.AbstractCSVWriter
import com.opencsv.CSVWriter
import model.SimModel
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object DataExporter {

    /**
     * CSV path leads to a directory, where csv files will be stored.
     */
    fun exportTo(csvPath: String, model: SimModel) {
        val p = Paths.get(csvPath)
        if(!Files.exists(p)) {
            val success = File(csvPath).mkdirs()
            if (!success) throw Exception()
        }
        val simInfoWriter = CSVWriter(FileWriter(File(csvPath, "model.csv"), true),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)
        simInfoWriter.writeNext(arrayOf(model.tick.toString(), model.vehicles.size.toString(), model.epochCount.toString()))
    }
}