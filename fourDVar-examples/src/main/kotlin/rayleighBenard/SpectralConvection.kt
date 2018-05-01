package rayleighBenard

import fourDVar.IModel
import io.improbable.keanu.kotlin.ArithmeticDouble
import io.improbable.keanu.kotlin.DoubleOperators
import io.improbable.keanu.randomFactory.RandomFactory
import io.improbable.keanu.vertices.dbl.DoubleVertex
import io.improbable.keanu.vertices.dbl.probabilistic.GaussianVertex
import java.util.ArrayList

class SpectralConvection<DOUBLE : DoubleOperators<DOUBLE>>(X : DOUBLE, Y : DOUBLE, Z : DOUBLE,
                                                           val random: RandomFactory<DOUBLE>):
        SpectralToPhysicalConverter<DOUBLE>(X,Y,Z), IModel<DOUBLE> {

    override fun runWindow(): Collection<DOUBLE> {
        val observations = ArrayList<DOUBLE>(MICROSTEPS_PER_STEP)
        for (i in 1..MICROSTEPS_PER_STEP) {
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

    override fun getGaussianState(): Collection<GaussianVertex> {
        return getState() as Collection<GaussianVertex>

    }

    override fun setState(state: Collection<DOUBLE>) {
        val it = state.iterator()
        x = it.next()
        y = it.next()
        z = it.next()
    }

    fun getMeanState(probabiliticState: Collection<DoubleVertex>): SpectralToPhysicalConverter<ArithmeticDouble> {
        val it = probabiliticState.iterator()
        return SpectralToPhysicalConverter(
                ArithmeticDouble(it.next().value),
                ArithmeticDouble(it.next().value),
                ArithmeticDouble(it.next().value))
    }

    fun getFluidState() : SpectralToPhysicalConverter<DOUBLE> {
        return this
    }

    companion object {
        val MICROSTEPS_PER_STEP = 5
    }
}
