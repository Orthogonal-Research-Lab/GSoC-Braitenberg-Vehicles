import tornadofx.*
import kotlin.math.abs
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test as test

class VisualizationTest {

    @test
    fun `rotation of vector doesn't change its length`() {
        val vec = DoubleVector(2.0, 4.0)
        val originalLength = vec.length
        vec.rotate(90.deg)
        assertTrue { abs(vec.length - originalLength) < 0.1 }
        vec.rotate(60.deg)
        assertTrue { abs(vec.length - originalLength) < 0.1 }
    }
}