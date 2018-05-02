package fourDVar

import io.improbable.keanu.kotlin.DoubleOperators

interface IModel<DATA : DoubleOperators<DATA>> {
    fun runWindow() : Collection<DATA>
    fun step()
    fun getState() : Collection<DATA>
    fun setState(state : Collection<DATA>)
}