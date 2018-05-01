package fourDVar

import io.improbable.keanu.kotlin.DoubleOperators
import io.improbable.keanu.vertices.dbl.probabilistic.GaussianVertex

interface IModel<DATA : DoubleOperators<DATA>> {
    fun runWindow() : Collection<DATA>
    fun step()
    fun getState() : Collection<DATA>
    fun setState(state : Collection<DATA>)
    fun getGaussianState(): Collection<GaussianVertex>
}