package io.improbable.swayze.finiteDifference

import io.improbable.keanu.kotlin.DoubleOperators

interface IBoundary<DATA : DoubleOperators<DATA>> {

    fun get(i: Int): DATA
    fun attachTo(f: IArray2D<DATA>)
    fun clone(): IBoundary<DATA>
}