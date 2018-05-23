package fourDVar

import io.improbable.keanu.vertices.dbl.DoubleVertex

interface IAssimilator<T: DoubleVertex> {
    fun assimilate(dbNet: DynamicBayesNet<T>)
    fun cycle(dbNet: DynamicBayesNet<T>, bestFit: Map<DoubleVertex, T>)
}