package io.improbable.swayze.finiteDifference

import io.improbable.keanu.kotlin.DoubleOperators

class ShiftS<DATA : DoubleOperators<DATA>>(var field: IField<DATA>) : IField<DATA> {

    override var domain = field.domain

    override fun get(i: Int, j: Int): DATA {
        if (j < field.jSize() - 1) {
            return field.get(i, j + 1)
        }
        return field.domain.NBoundary.get(field.iSize() - 1 - i)
    }

    override fun set(i: Int, j: Int, v: DATA) {
        if (j < field.jSize() - 1) {
            field.set(i, j + 1, v)
        } else {
            throw(IndexOutOfBoundsException("Trying to set the value of a boundary via a finiteDifference.field.ShiftS field"))
        }
    }

    override fun iSize(): Int {
        return field.iSize()
    }

    override fun jSize(): Int {
        return field.jSize()
    }

}


