package io.improbable.swayze.finiteDifference

import io.improbable.keanu.kotlin.DoubleOperators

class ShiftE<DATA : DoubleOperators<DATA>>(var field: IField<DATA>) : IField<DATA> {

    override var domain = field.domain

    override fun get(i: Int, j: Int): DATA {
        if (i > 0) {
            return field.get(i - 1, j)
        }
        return field.domain.WBoundary.get(field.jSize() - 1 - j)
    }

    override fun set(i: Int, j: Int, v: DATA) {
        if (i > 0) {
            field.set(i - 1, j, v)
        } else {
            throw(IndexOutOfBoundsException("Trying to set the value of a boundary via a finiteDifference.field.ShiftE field"))
        }
    }

    override fun iSize(): Int {
        return field.iSize()
    }

    override fun jSize(): Int {
        return field.jSize()
    }

}


