package Lorenz4DVar

import fourDVar.FourDVar
import fourDVar.DynamicBayesNet
import io.improbable.keanu.kotlin.ArithmeticDouble
import io.improbable.keanu.randomFactory.DoubleVertexFactory
import io.improbable.keanu.randomFactory.RandomDoubleFactory
import lorenz.LorenzModel

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

        val realWorld = LorenzModel(
                ArithmeticDouble(startX),
                ArithmeticDouble(startY),
                ArithmeticDouble(startZ),
                random)

        val probabilistic = DoubleVertexFactory()

        val probabilisticModel = LorenzModel(
                probabilistic.nextGaussian(startX + 0.01, initialObservationError),
                probabilistic.nextGaussian(startY + 0.01, initialObservationError),
                probabilistic.nextGaussian(startZ + 0.01, initialObservationError),
                probabilistic)

        val dynamicBayesNet = DynamicBayesNet(probabilisticModel)
        val variationalBayes = FourDVar()

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