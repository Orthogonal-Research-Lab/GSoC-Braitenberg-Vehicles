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
        val fh = File(csvPath, "model.csv")
        if (!fh.exists()) fh.createNewFile()
        val lines = fh.readLines()
        val append = lines.isEmpty() || lines[lines.size - 1].startsWith("${model.tick - 1}")
        val simInfoWriter = CSVWriter(FileWriter(fh, append),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)
        if (fh.length() == 0L) simInfoWriter.writeNext(arrayOf("Tick #", "Vehicles", "Epoch #"))
        simInfoWriter.writeNext(arrayOf(model.tick.toString(), model.vehicles.size.toString(), model.epochCount.toString()))
        simInfoWriter.close()
    }
}