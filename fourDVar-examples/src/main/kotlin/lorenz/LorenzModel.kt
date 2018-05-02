package lorenz

import fourDVar.IModel
import io.improbable.keanu.kotlin.DoubleOperators
import temporary.RandomFactory
import java.util.ArrayList

class LorenzModel<DOUBLE: DoubleOperators<DOUBLE>>(val startState: Collection<DOUBLE>,
                                                   val random: RandomFactory<DOUBLE>) : IModel<DOUBLE> {

    lateinit var x: DOUBLE
    lateinit var y: DOUBLE
    lateinit var z: DOUBLE

    init {
        setState(startState)
    }

    val SIGMA = 10.0
    val BETA = 2.66667
    val RHO = 28.0
    val DT = 0.01

    val MICROSTEPS_PER_STEP = 5
    val OBSERVATION_ERROR = 1.0

    override fun runWindow(): Collection<DOUBLE> {
        val observations = ArrayList<DOUBLE>(MICROSTEPS_PER_STEP)
        for (i in 0 until MICROSTEPS_PER_STEP) {
            step()
            observations.add(observe())
        }
        return observations
    }

    override fun step() {
        val dx = ((y - x) * SIGMA) * DT
        val dy = (x * (z - RHO) + y) * (-DT)
        val dz = (x * y - z * BETA) * DT
        x += dx
        y += dy
        z += dz
    }

    private fun observe(): DOUBLE {
        return random.nextGaussian(x, OBSERVATION_ERROR)
    }

    override fun getState(): Collection<DOUBLE> {
        return listOf(x, y, z)
    }

    override fun setState(state: Collection<DOUBLE>) {
        val it = state.iterator()
        x = it.next()
        y = it.next()
        z = it.next()
    }
}