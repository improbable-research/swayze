package io.improbable.swayze.finiteDifference

import io.improbable.keanu.kotlin.DoubleOperators

class BinaryOpField<DATA : DoubleOperators<DATA>>(var f: IField<DATA>,
                                                  var g: IField<DATA>,
                                                  var op: (DATA, DATA) -> DATA) : IField<DATA> {

    override var domain = f.domain

    override fun get(i: Int, j: Int): DATA {
        return op(f.get(i, j), g.get(i, j))
    }

    override fun set(i: Int, j: Int, v: DATA) {
        f[i, j] = v
        g[i, j] = v
    }

    override fun iSize(): Int {
        return f.iSize()
    }

    override fun jSize(): Int {
        return f.jSize()
    }


}