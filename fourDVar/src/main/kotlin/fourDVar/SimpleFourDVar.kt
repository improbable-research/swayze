package fourDVar

import io.improbable.keanu.algorithms.variational.GradientOptimizer
import io.improbable.keanu.vertices.dbl.DoubleVertex
import io.improbable.keanu.vertices.dbl.probabilistic.GaussianVertex

class SimpleFourDVar(var MAX_MAP_EVALUATIONS: Int, var BEST_FIT_SIGMA: Double) : IAssimilator<DoubleVertex> {

    constructor(): this(2000, 1.0)

    override fun assimilate(dbNet: DynamicBayesNet<DoubleVertex>) {
        val bestFit = simpleAssimilate(dbNet)
        cycle(dbNet, bestFit)
    }

    fun simpleAssimilate(dbNet: DynamicBayesNet<DoubleVertex>): HashMap<DoubleVertex, DoubleVertex> {

        val graphOptimizer = GradientOptimizer(dbNet.net)
        graphOptimizer.maxAPosteriori(MAX_MAP_EVALUATIONS)

        val endStateBestFit = HashMap<DoubleVertex, DoubleVertex>()
        for (vertex in dbNet.endState) {
            endStateBestFit[vertex] = GaussianVertex(vertex.value, BEST_FIT_SIGMA)
        }

        var error = 0.0
        for ()

        return endStateBestFit
    }

    override fun cycle(dbNet: DynamicBayesNet<DoubleVertex>, bestFit: Map<DoubleVertex, DoubleVertex>) {


    }
}