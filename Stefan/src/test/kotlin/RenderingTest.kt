import org.junit.jupiter.api.BeforeEach
import tornadofx.*
import kotlin.math.abs
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test as test

class RenderingTest {

//    val testWorld = SimModel(worldWidth = 800.0, worldHeight = 800.0, matingRate = 0.2, rateLuckySelected = 0.01,
//        mutationRate = 0.05, objects = WorldObject<>)

    @BeforeEach
    fun init(){

    }

    private fun initSimWorld() {

    }

    @test
    fun `rotation of vector doesn't change its length`() {
        val vec = DoubleVector(2.0, 4.0)
        val originalLength = vec.length
        vec.rotate(90.deg)
        assertTrue { abs(vec.length - originalLength) < 0.1 }
        vec.rotate(60.deg)
        assertTrue { abs(vec.length - originalLength) < 0.1 }
    }

    @test
    fun `vector rotates`() {
        val vec = DoubleVector(2.0, 0.0)
        vec.rotate(90.deg)
        assertTrue(vec.x - 0.0 < 0.001)
    }

    @test
    fun `world walls do not let vehicles get beyond boundaries`() {

    }
}
