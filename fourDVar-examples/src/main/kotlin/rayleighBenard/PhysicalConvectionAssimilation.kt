package rayleighBenard

import io.improbable.swayze.finiteDifference.Boundary
import io.improbable.swayze.finiteDifference.Domain
import fourDVar.DynamicBayesNet
import fourDVar.GaussianFourDVar
import gnuPlotLib.PlotField
import io.improbable.keanu.kotlin.ArithmeticDouble
import temporary.DoubleVertexFactory
import temporary.RandomDoubleFactory
import io.improbable.keanu.vertices.dbl.probabilistic.GaussianVertex

fun main(args: Array<String>) {

    // TODO tidy these up into a container
    var XSIZE = 10 //80
    var YSIZE = 10 //28
    val DX = 2.0 / (XSIZE * 0.707106781)
    val DY = 1.0 / (YSIZE + 1.0)

    val domain = Domain(DX, DY,
            Boundary.Dirichlet(ArithmeticDouble(0.0)),
            Boundary.Dirichlet(ArithmeticDouble(0.0)),
            Boundary.Periodic(),
            Boundary.Periodic())

    val random = RandomDoubleFactory()
    var realStartState = listOf(
            ArithmeticDouble(20.0),
            ArithmeticDouble(19.0),
            ArithmeticDouble(50.0))
    val realWorld = PhysicalConvection(realStartState, domain, random)

    // TODO this n'est pas de all that handsome
    val probabilisticDomain = Domain(DX, DY,
            Boundary.Dirichlet(GaussianVertex(0.0, 1.0)),
            Boundary.Dirichlet(GaussianVertex(0.0, 1.0)),
            Boundary.Periodic(),
            Boundary.Periodic())

    val probabilistic = DoubleVertexFactory()
    var probabilisticStartState = listOf(
            probabilistic.nextGaussian(20.5, 1.0),
            probabilistic.nextGaussian(19.0, 1.0),
            probabilistic.nextGaussian(50.0, 1.0))
    val probabilisticModel = PhysicalConvection(probabilisticStartState, probabilisticDomain, probabilistic)

    val bayesNetOfModel = DynamicBayesNet<GaussianVertex>(probabilisticModel, probabilisticStartState)
    println("Constructed initial Bayesian Network")
    val fourDVar = GaussianFourDVar(1)

    var plot = PlotField()

    for (window in 0 until 200) {
        println("\nRunning window...")
        val observations = realWorld.runWindow()
        println("\nAdding observations...")
        bayesNetOfModel.addObservations((observations))
        println("\nAssimilating...")
        fourDVar.assimilate(bayesNetOfModel)
        println("\nAssimilation complete... Well done Dave")

        plot.linePlot(plot.scalarToGNUplotMatrix(realWorld.psi))

        println("" + realWorld.ita[2,2] + " " + probabilisticModel.ita[2,2])
        println("Running window: " + window)
    }

//    plot.stop()
    println("Doneso")
}