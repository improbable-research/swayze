package fourDVar

import io.improbable.keanu.algorithms.mcmc.MetropolisHastings
import io.improbable.keanu.algorithms.variational.GradientOptimizer
import io.improbable.keanu.vertices.dbl.DoubleVertex
import io.improbable.keanu.vertices.dbl.probabilistic.GaussianVertex
import java.util.*

class GaussianFourDVar(var MAX_MAP_EVALUATIONS: Int, var POSTERIOR_SAMPLE_COUNT: Int, var BEST_FIT_SIGMA: Double) {

    constructor() : this(2000, 50, 1.0)

    constructor(MAX_MAP_EVALUATIONS: Int) : this(MAX_MAP_EVALUATIONS, 50, 1.0)

    fun assimilate(dbNet: DynamicBayesNet<GaussianVertex>) {
        val bestFit = variationalBayes(dbNet)
        cycle(dbNet, bestFit)
    }

    private fun variationalBayes(dbNet: DynamicBayesNet<GaussianVertex>): HashMap<DoubleVertex, GaussianVertex> {
        println("- Creating graph optimiser...")
        val graphOptimizer = GradientOptimizer(dbNet.net)
        println("- Determining MAP with " + MAX_MAP_EVALUATIONS + " evaluations")
        graphOptimizer.maxAPosteriori(MAX_MAP_EVALUATIONS)
        println("- Fully MAPed up...")

        val endStateBestFit = HashMap<DoubleVertex, GaussianVertex>()
        for (vertex in dbNet.endState) {
            endStateBestFit[vertex] = GaussianVertex(vertex.value, BEST_FIT_SIGMA)
        }
        val endStateList = arrayListOf<DoubleVertex>()
        dbNet.endState.forEach { dv -> endStateList.add(dv) }

        println("- Performing Metropolis Hastings")
        val samples = MetropolisHastings.getPosteriorSamples(dbNet.net, endStateList, POSTERIOR_SAMPLE_COUNT)

        for (vertex in dbNet.endState) {
            var xbar = 0.0
            var x2bar = 0.0
            for (sample in samples[vertex].asList()) {
                xbar += sample
                x2bar += sample * sample
            }
            val n = samples[vertex].asList().size
            xbar /= n * Math.sqrt((n - 1.0) / n)
            x2bar /= n - 1
            endStateBestFit.getValue(vertex).sigma.value = Math.sqrt(x2bar - xbar * xbar + 0.05)
        }
        return endStateBestFit
    }

    private fun cycle(dbNet: DynamicBayesNet<GaussianVertex>, bestFit: Map<DoubleVertex, GaussianVertex>) {
        val endVertexIt = dbNet.endState.iterator()
        for (startVertex in dbNet.startState) {
            if (!endVertexIt.hasNext()) throw(ArrayIndexOutOfBoundsException("start and end states don't have the same dimension, strange."))

            val endVertex = endVertexIt.next()
            val bestFitVertex = bestFit.getValue(endVertex)

            startVertex.mu.value = bestFitVertex.mu.value
            startVertex.sigma.value = bestFitVertex.sigma.value
        }
        for (startVertex in dbNet.startState) {
            startVertex.setAndCascade(startVertex.mu.value)
        }
    }
}