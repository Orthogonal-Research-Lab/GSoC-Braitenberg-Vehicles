import agent.brain.Network
import org.junit.jupiter.api.BeforeEach
import kotlin.random.Random
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test as test


@ExperimentalUnsignedTypes
class NetworkTest {

    lateinit var simpleNet: Network

    @BeforeEach
    fun init() {
        simpleNet = Network.smallAutoencoder()
    }

    @test
    fun `compiles given a small default network`() {
        val motorOut =
            simpleNet.propagate(
                arrayOf(
                    DoubleVector(1.0, 1.0), DoubleVector(1.0, 1.0)
                )
            )

        println("processed signal: ${motorOut.map { it.toString() }}")
    }

    @test
    fun `throws exception when signal vector is wrong length`() {
        assertFailsWith<WrongLengthException> {
            simpleNet.propagate(arrayOf(DoubleVector(1.0, 1.0), DoubleVector(0.5, 0.5), DoubleVector(1.0, 0.5)))
        }
    }

    @test
    fun `throws exception when matrix is not circuit-free`() {
        val r = Random(586)
        val adjMatrix = Matrix<Double?>(3, 3,
            arrayOf(arrayOf(0.0, 0.0, r.nextDouble()),
            arrayOf(r.nextDouble(), 0.0, 0.0),
            arrayOf(r.nextDouble(), r.nextDouble(), 0.0)) as Array<Array<Double?>>)
        assertFailsWith<AdjacencyMatrixCircuitsPossible> {
            Network.fromAdjMatrix(adjMatrix)
        }
    }

    @test
    fun `run code`() { //DEBUG
    }
}