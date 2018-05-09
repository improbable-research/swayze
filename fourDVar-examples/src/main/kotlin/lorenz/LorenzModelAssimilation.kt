package Lorenz4DVar

import fourDVar.DynamicBayesNet
import fourDVar.GaussianFourDVar
import io.improbable.keanu.kotlin.ArithmeticDouble
import temporary.RandomDoubleFactory
import io.improbable.keanu.vertices.dbl.probabilistic.GaussianVertex
import lorenz.LorenzModel
import temporary.DoubleVertexFactory

class LorenzModelAssimilation() {

    var startX = 20.0
    var startY = 19.0
    var startZ = 50.0
    var initialObservationError = 1.0

    constructor (startX: Double, startY: Double, startZ: Double, initialObservationError: Double): this() {
        this.startX = startX
        this.startY = startY
        this.startZ = startZ
        this.initialObservationError = initialObservationError
    }

    fun run(NUMBER_OF_WINDOWS: Int) {

        val random = RandomDoubleFactory()

        val realStartState = listOf(
                ArithmeticDouble(startX),
                ArithmeticDouble(startY),
                ArithmeticDouble(startZ))

        val realWorld = LorenzModel(realStartState, random)

        val probabilistic = DoubleVertexFactory()

        val probabilisticStartState = listOf(
                probabilistic.nextGaussian(startX + 0.01, initialObservationError),
                probabilistic.nextGaussian(startY + 0.01, initialObservationError),
                probabilistic.nextGaussian(startZ + 0.01, initialObservationError))

        val probabilisticModel = LorenzModel(probabilisticStartState, probabilistic)
        val dynamicBayesNet = DynamicBayesNet<GaussianVertex>(probabilisticModel, probabilisticStartState)
        val variationalBayes = GaussianFourDVar()

        for (windowNumber in 0 until NUMBER_OF_WINDOWS) {
            val observations = realWorld.runWindow()
            dynamicBayesNet.addObservations(observations)
            variationalBayes.assimilate(dynamicBayesNet)

            // Having performed inference during the assimilation step our startState are now posterior distributions given the observations
            val it = dynamicBayesNet.getModeOfWindowStartState().iterator()
            System.out.println("Real World:\t\t\t\t" + realWorld.x.value + "\nProbabilistic Model:\t" + it.next() + "\n")
        }
    }
}

fun main(args: Array<String>) {

    val assimilation = LorenzModelAssimilation()
    assimilation.run(10)
}