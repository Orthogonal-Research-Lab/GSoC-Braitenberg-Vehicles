import agent.brain.BinaryRepresentation
import agent.brain.Network
import java.util.*
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test as test


class NetworkTest {

    @test
    fun compiles() {
        val net = Network.smallAutoencoder()
        val motorOut =
            net.propagate(
                arrayOf(
                    DoubleVector(1.0, 1.0), DoubleVector(1.0, 1.0)
                )
            )

        println("processed signal: ${motorOut.map { it.toString() }}")
    }

    @test
    fun `run code`() { //DEBUG
        println(Arrays.toString(0.0.bytes()))
    }

    @test
    fun `binary representation chunks convert to double`() {
        val br = BinaryRepresentation.of(0.754)
        val initialBytes = br.toByteArray()
        val slice = br.get(0, 64).toByteArray()
        for (i in 0 until initialBytes.size) {
            assertEquals(initialBytes[i], slice[i])
        }
    }
}