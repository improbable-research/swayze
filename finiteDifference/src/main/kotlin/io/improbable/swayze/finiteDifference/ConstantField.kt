package io.improbable.swayze.finiteDifference

import io.improbable.keanu.kotlin.DoubleOperators

class ConstantField<DATA : DoubleOperators<DATA>>(var i_size: Int, var j_size: Int, override var domain: Domain<DATA>,
                                                  var v: DATA) : IField<DATA> {

    constructor(isize: Int, jsize: Int, dx: Double, dy: Double, v: DATA)
            : this(isize, jsize, Domain<DATA>(dx, dy,
            Boundary.Periodic(), Boundary.Periodic(),
            Boundary.Periodic(), Boundary.Periodic()), v)

//    override fun getDomain(): Domain<DATA> {
//        return domain
//    }

    override fun get(i: Int, j: Int): DATA {
        return v
    }

    override fun set(i: Int, j: Int, v: DATA) {
        this[i, j] = v
    }

    override fun iSize(): Int {
        return i_size
    }

    override fun jSize(): Int {
        return j_size
    }
}