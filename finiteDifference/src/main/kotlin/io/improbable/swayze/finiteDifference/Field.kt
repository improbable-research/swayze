package io.improbable.swayze.finiteDifference

import io.improbable.keanu.kotlin.DoubleOperators

class Field<DATATYPE : DoubleOperators<DATATYPE>> : Array2D<DATATYPE>, IField<DATATYPE> {

    override var domain: Domain<DATATYPE>

    constructor(xSize: Int, ySize: Int, domain: Domain<DATATYPE>, vals: (Int, Int) -> DATATYPE) :
            super(xSize, ySize, vals) {
        this.domain = domain.clone()
        attachBoundaries()
    }

    constructor(iField: IField<DATATYPE>) :
            super(iField.iSize(), iField.jSize(), { i, j -> iField.get(i, j) }) {
        //this.domain = iField.getDomain().clone()
        this.domain = iField.domain.clone()
        attachBoundaries()
    }

    constructor(array2D: Array2D<DATATYPE>, domain: Domain<DATATYPE>) :
            super(array2D.iSize(), array2D.jSize(), { i, j -> array2D.get(i, j) }) {
        this.domain = domain
        attachBoundaries()
    }

    private fun attachBoundaries() {
        domain.SBoundary.attachTo(this);
        domain.NBoundary.attachTo(Rotate180(this))
        domain.EBoundary.attachTo(RotateClockwise90(this))
        domain.WBoundary.attachTo(RotateAntiClockwise90(this))
    }

    operator fun plusAssign(other: IArray2D<DATATYPE>) {
        for (i in 0 until iSize()) {
            for (j in 0 until jSize()) {
                set(i, j, get(i, j) + other.get(i, j))
            }
        }
    }

    fun set(other: IField<DATATYPE>) {
        for (i in 0 until iSize()) {
            for (j in 0 until jSize()) {
                set(i, j, other.get(i, j))
            }
        }
    }
}

