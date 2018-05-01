package rayleighBenard

import io.improbable.swayze.finiteDifference.Boundary
import io.improbable.swayze.finiteDifference.Domain
import fourDVar.DynamicBayesNet
import fourDVar.FourDVar
import gnuPlotLib.PlotField
import io.improbable.keanu.kotlin.ArithmeticDouble
import io.improbable.keanu.randomFactory.DoubleVertexFactory
import io.improbable.keanu.randomFactory.RandomDoubleFactory
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

    val realWorld = PhysicalConvection(
            ArithmeticDouble(20.0),
            ArithmeticDouble(19.0),
            ArithmeticDouble(50.0),
            domain,
            random)

    // TODO this n'est pas de all that handsome
    val probabilisticDomain = Domain(DX, DY,
            Boundary.Dirichlet(GaussianVertex(0.0, 1.0)),
            Boundary.Dirichlet(GaussianVertex(0.0, 1.0)),
            Boundary.Periodic(),
            Boundary.Periodic())

    val probabilistic = DoubleVertexFactory()
    val probabilisticModel = PhysicalConvection(
            probabilistic.nextGaussian(20.5, 1.0),
            probabilistic.nextGaussian(19.0, 1.0),
            probabilistic.nextGaussian(50.0, 1.0),
            probabilisticDomain,
            probabilistic)

    val bayesNetOfModel = DynamicBayesNet(probabilisticModel)
    val fourDVar = FourDVar()

//    var plot = PlotField()

    for (window in 0 until 200) {
        val observations = realWorld.runWindow()
        bayesNetOfModel.addObservations((observations))
        fourDVar.assimilate(bayesNetOfModel)

//        plot.linePlot(plot.scalarToGNUplotMatrix(realWorld.psi))

        println("" + realWorld.ita[20,20] + " " + probabilisticModel.ita[20,20])
        println("Running window: " + window)
    }

//    plot.stop()
    println("Doneso")
}