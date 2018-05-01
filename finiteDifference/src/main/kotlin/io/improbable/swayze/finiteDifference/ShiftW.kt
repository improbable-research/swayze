package io.improbable.swayze.finiteDifference

import io.improbable.keanu.kotlin.DoubleOperators

class ShiftW<DATA : DoubleOperators<DATA>>(var field: IField<DATA>) : IField<DATA> {

    override var domain = field.domain

    override fun get(i: Int, j: Int): DATA {
        if (i < field.iSize() - 1) {
            return field.get(i + 1, j)
        }
        return field.domain.EBoundary.get(j)
    }

    override fun set(i: Int, j: Int, v: DATA) {
        if (i < field.iSize() - 1) {
            field.set(i + 1, j, v)
        } else {
            throw(IndexOutOfBoundsException("Trying to set the value of a boundary via a finiteDifference.field.ShiftW field"))
        }
    }

    override fun iSize(): Int {
        return field.iSize()
    }

    override fun jSize(): Int {
        return field.jSize()
    }

}
