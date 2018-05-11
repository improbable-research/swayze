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
import io.improbable.swayze.finiteDifference.FieldParams

fun main(args: Array<String>) {

    val XSIZE = 10 //80
    val YSIZE = 10 //28
    val DX = 2.0 / (XSIZE * 0.707106781)
    val DY = 1.0 / (YSIZE + 1.0)

    var params = FieldParams(XSIZE, YSIZE,0.01, DX, DY, 0.00001)

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
    val realWorld = PhysicalConvection(realStartState, domain, params, random)

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
    val probabilisticModel = PhysicalConvection(probabilisticStartState, probabilisticDomain, params, probabilistic)

    val bayesNetOfModel = DynamicBayesNet<GaussianVertex>(probabilisticModel, probabilisticStartState)
    val fourDVar = GaussianFourDVar()

    var plot = PlotField()

    for (window in 0 until 200) {
        val start = System.currentTimeMillis()
        println("Running window")
        val observations = realWorld.runWindow()
        println("- - - Window run time: " + (System.currentTimeMillis() - start))
        bayesNetOfModel.addObservations((observations))
        fourDVar.assimilate(bayesNetOfModel)

        plot.linePlot(plot.scalarToGNUplotMatrix(realWorld.psi, probabilisticModel.psi))

        println("- " + realWorld.ita[2,2].value + " " + probabilisticModel.ita[2,2].value)
        println("Running window: " + window)
    }

//    plot.stop()
    println("Doneso")
}