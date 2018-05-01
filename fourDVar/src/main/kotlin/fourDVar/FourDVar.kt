package fourDVar

import io.improbable.keanu.algorithms.mcmc.MetropolisHastings
import io.improbable.keanu.algorithms.variational.GradientOptimizer
import io.improbable.keanu.vertices.dbl.DoubleVertex
import io.improbable.keanu.vertices.dbl.probabilistic.GaussianVertex
import java.util.*

class FourDVar {

    private var MAX_MAP_EVALUATIONS = 2000
    private var POSTERIOR_SAMPLE_COUNT = 50
    private var BEST_FIT_SIGMA = 1.0

    fun FourDVar() {}

    fun FourDVar(MAX_MAP_EVALUATIONS: Int) {
        this.MAX_MAP_EVALUATIONS = MAX_MAP_EVALUATIONS
    }

    fun FourDVar(MAX_MAP_EVALUATIONS: Int, POSTERIOR_SAMPLE_COUNT: Int, BEST_FIT_SIGMA: Double) {
        this.MAX_MAP_EVALUATIONS = MAX_MAP_EVALUATIONS
        this.POSTERIOR_SAMPLE_COUNT = POSTERIOR_SAMPLE_COUNT
        this.BEST_FIT_SIGMA = BEST_FIT_SIGMA
    }

    fun assimilate(dbNet: DynamicBayesNet) {
        val bestFit = variationalBayes(dbNet)
        cycle(dbNet, bestFit)
    }

    private fun variationalBayes(dbNet: DynamicBayesNet): HashMap<DoubleVertex, GaussianVertex> {
        val graphOptimizer = GradientOptimizer(dbNet.net)
        graphOptimizer.maxAPosteriori(MAX_MAP_EVALUATIONS)

        val endStateBestFit = HashMap<DoubleVertex, GaussianVertex>()
        for (vertex in dbNet.endState) {
            endStateBestFit[vertex] = GaussianVertex(vertex.value, BEST_FIT_SIGMA)
        }
        val endStateList = arrayListOf<DoubleVertex>()
        dbNet.endState.forEach { dv -> endStateList.add(dv) }

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

    private fun cycle(dbNet: DynamicBayesNet, bestFit: Map<DoubleVertex, GaussianVertex>) {
        val endVertexIt = dbNet.endState.iterator()
        for (startVertex in dbNet.startState) {
            if (!endVertexIt.hasNext()) throw(ArrayIndexOutOfBoundsException("start and end states don't have the same dimension, strange."))
            val endVertex = endVertexIt.next()

            if (startVertex is GaussianVertex) {
                val bestFitVertex = bestFit.getValue(endVertex)
                startVertex.mu.value = bestFitVertex.mu.value
                startVertex.sigma.value = bestFitVertex.sigma.value
            }
        }
        for (startVertex in dbNet.startState) {
            if (startVertex is GaussianVertex) {
                startVertex.setAndCascade(startVertex.mu.value)
            }
        }
    }
}