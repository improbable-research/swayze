package rayleighBenard

import fourDVar.DynamicBayesNet
import fourDVar.FourDVar
import io.improbable.keanu.kotlin.ArithmeticDouble
import io.improbable.keanu.randomFactory.DoubleVertexFactory
import io.improbable.keanu.randomFactory.RandomDoubleFactory

fun main(args: Array<String>) {

    val random = RandomDoubleFactory()
    val realWorld = SpectralConvection(
            ArithmeticDouble(20.0),
            ArithmeticDouble(19.0),
            ArithmeticDouble(50.0),
            random
    )

    val probabilistic = DoubleVertexFactory()
    val probabilisticModel = SpectralConvection(
            probabilistic.nextGaussian(20.5, 1.0),
            probabilistic.nextGaussian(19.5, 1.0),
            probabilistic.nextGaussian(50.3, 1.0),
            probabilistic
    )

    val bayesNetOfModel = DynamicBayesNet(probabilisticModel)
    val assimilationAlgorithm = FourDVar()

    val plot = PlotFluid()

    for (window in 1..750) {

        val observations = realWorld.runWindow()
        bayesNetOfModel.addObservations(observations)
        assimilationAlgorithm.assimilate(bayesNetOfModel)

        plot.temperature(realWorld.getFluidState(), probabilisticModel.getMeanState(bayesNetOfModel.startState))
    }

    plot.stop()
}

