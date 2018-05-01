package io.improbable.swayze.finiteDifference

import io.improbable.keanu.kotlin.DoubleOperators

class ShiftN<DATA : DoubleOperators<DATA>>(var field: IField<DATA>) : IField<DATA> {

    override var domain = field.domain

    override fun get(i: Int, j: Int): DATA {
        if (j > 0) {
            return field.get(i, j - 1)
        }
        return field.domain.SBoundary.get(i)
    }

    override fun set(i: Int, j: Int, v: DATA) {
        if (j > 0) {
            field.set(i, j - 1, v)
        } else {
            throw(IndexOutOfBoundsException("Trying to set the value of a boundary via a finiteDifference.field.ShiftN field"))
        }
    }

    override fun iSize(): Int {
        return field.iSize()
    }

    override fun jSize(): Int {
        return field.jSize()
    }

}

