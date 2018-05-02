package rayleighBenard

import fourDVar.DynamicBayesNet
import fourDVar.GaussianFourDVar
import io.improbable.keanu.kotlin.ArithmeticDouble
import temporary.DoubleVertexFactory
import temporary.RandomDoubleFactory
import io.improbable.keanu.vertices.dbl.probabilistic.GaussianVertex

fun main(args: Array<String>) {

    val random = RandomDoubleFactory()
    var realStartState = listOf(
            ArithmeticDouble(20.0),
            ArithmeticDouble(19.0),
            ArithmeticDouble(50.0))
    val realWorld = SpectralConvection(realStartState, random)

    val probabilistic = DoubleVertexFactory()
    val probabilisticStartState = listOf(
            probabilistic.nextGaussian(20.5, 1.0),
            probabilistic.nextGaussian(19.5, 1.0),
            probabilistic.nextGaussian(50.3, 1.0))
    var probabilisticModel = SpectralConvection(probabilisticStartState, probabilistic)

    val bayesNetOfModel = DynamicBayesNet<GaussianVertex>(probabilisticModel, probabilisticStartState)
    val assimilationAlgorithm = GaussianFourDVar()

    val plot = PlotFluid()

    for (window in 1..750) {

        val observations = realWorld.runWindow()
        bayesNetOfModel.addObservations(observations)
        assimilationAlgorithm.assimilate(bayesNetOfModel)

        plot.temperature(realWorld.getFluidState(), probabilisticModel.getMeanState(bayesNetOfModel.startState))
    }

    plot.stop()
}

