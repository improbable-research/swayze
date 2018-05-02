package fourDVar

import io.improbable.keanu.kotlin.ArithmeticDouble
import io.improbable.keanu.network.BayesNet
import io.improbable.keanu.vertices.dbl.DoubleVertex
import io.improbable.keanu.vertices.dbl.probabilistic.GaussianVertex

class DynamicBayesNet<T : DoubleVertex>(probabilisticModel: IModel<DoubleVertex>, var startState: List<T>) {

    init {
        probabilisticModel.setState(startState)
    }

    var observationVertices = probabilisticModel.runWindow()
    var endState = probabilisticModel.getState()
    var net = BayesNet(startState.iterator().next().connectedGraph)

    private fun getProbabilisticVertices(): Collection<DoubleVertex> {
        val vertices = ArrayList<DoubleVertex>(startState.count() + endState.count() + observationVertices.count())
        vertices.addAll(startState)
        vertices.addAll(endState)
        vertices.addAll(observationVertices)
        return vertices
    }

    fun addObservations(observations: Iterable<ArithmeticDouble>) {
        val observation = observations.iterator()
        for (vertex in observationVertices) {
            if (!observation.hasNext()) throw(ArrayIndexOutOfBoundsException("wrong number of observations"))
            vertex.observe(observation.next().value)
        }
    }

    fun getModeOfWindowStartState (): Collection<Double> {
        val it = startState.iterator()
        val posteriorWindowStartState = ArrayList<Double>(startState.count())
        while (it.hasNext()) {
            val ss = it.next() as GaussianVertex
            posteriorWindowStartState.add(ss.mu.value)
        }
        return posteriorWindowStartState
    }
}