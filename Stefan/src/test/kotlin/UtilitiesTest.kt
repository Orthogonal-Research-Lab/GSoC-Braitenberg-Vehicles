import com.opencsv.CSVWriter
import java.io.File
import java.io.FileWriter
import org.junit.jupiter.api.Test as test
class UtilitiesTest {

    @test fun `csv is written in append mode by csv writer`() {
        val tw = CSVWriter(
            FileWriter(File("src/test/res/csvtest.csv"), true),
            CSVWriter.DEFAULT_SEPARATOR,
            CSVWriter.NO_QUOTE_CHARACTER,
            CSVWriter.DEFAULT_ESCAPE_CHARACTER,
            CSVWriter.DEFAULT_LINE_END)
        tw.writeNext(arrayOf("This", "is", "1st", "line"))
        tw.writeNext(arrayOf("This", "is", "2nd", "line"))
        tw.close()
    }

}