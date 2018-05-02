package rayleighBenard

import fourDVar.IModel
import io.improbable.keanu.kotlin.ArithmeticDouble
import io.improbable.keanu.kotlin.DoubleOperators
import temporary.RandomFactory
import io.improbable.keanu.vertices.dbl.DoubleVertex
import java.util.ArrayList

class SpectralConvection<DOUBLE : DoubleOperators<DOUBLE>>(startState: Collection<DOUBLE>,
                                                           val random: RandomFactory<DOUBLE>):
        SpectralToPhysicalConverter<DOUBLE>(startState), IModel<DOUBLE> {

    init {
        setState(startState)
    }

    override fun runWindow(): Collection<DOUBLE> {
        val observations = ArrayList<DOUBLE>(STEPS_PER_WINDOW)
        for (i in 1..STEPS_PER_WINDOW) {
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

    private fun observe() : DOUBLE {
        val temp = getDeltaT(0.5,0.5)
        return random.nextGaussian(temp, 1.0)
    }


    override fun getState() : Collection<DOUBLE> {
        return listOf(x, y, z)
    }

    override fun setState(state: Collection<DOUBLE>) {
        val it = state.iterator()
        x = it.next()
        y = it.next()
        z = it.next()
    }

    fun getMeanState(probabiliticState: Collection<DoubleVertex>): SpectralToPhysicalConverter<ArithmeticDouble> {
        var it = probabiliticState.iterator()
        return SpectralToPhysicalConverter(listOf(
                ArithmeticDouble(it.next().value),
                ArithmeticDouble(it.next().value),
                ArithmeticDouble(it.next().value)))
    }

    fun getFluidState() : SpectralToPhysicalConverter<DOUBLE> {
        return this
    }

    companion object {
        val STEPS_PER_WINDOW = 5
    }
}
