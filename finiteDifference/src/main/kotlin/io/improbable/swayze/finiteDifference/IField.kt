package io.improbable.swayze.finiteDifference

import io.improbable.keanu.kotlin.DoubleOperators

interface IField<DATATYPE : DoubleOperators<DATATYPE>> : IArray2D<DATATYPE>, DoubleOperators<IField<DATATYPE>> {

    var domain: Domain<DATATYPE>

    override fun plus(other: IField<DATATYPE>): IField<DATATYPE> {
        return BinaryOpField(this, other, { i, j -> i + j })
    }

    override fun minus(other: IField<DATATYPE>): IField<DATATYPE> {
        return BinaryOpField(this, other, { i, j -> i - j })
    }

    override fun div(other: IField<DATATYPE>): IField<DATATYPE> {
        return BinaryOpField(this, other, { i, j -> i / j })
    }

    override fun times(other: IField<DATATYPE>): IField<DATATYPE> {
        return BinaryOpField(this, other, { i, j -> i * j })
    }

    override fun div(other: Double): IField<DATATYPE> {
        return DoubleOpField(this, other, { i, j -> i / j })
    }

    override fun times(other: Double): IField<DATATYPE> {
        return DoubleOpField(this, other, { i, j -> i * j })
    }

    override fun plus(other: Double): IField<DATATYPE> {
        return DoubleOpField(this, other, { i, j -> i + j })
    }

    override fun minus(other: Double): IField<DATATYPE> {
        return DoubleOpField(this, other, { i, j -> i - j })
    }

    override fun unaryMinus(): IField<DATATYPE> {
        return UnaryOpField(this, { i -> -i })
    }

    override fun sin(): IField<DATATYPE> {
        return UnaryOpField(this, { i -> i.sin() })
    }

    override fun asin(): IField<DATATYPE> {
        return UnaryOpField(this, { i -> i.asin() })
    }

    override fun cos(): IField<DATATYPE> {
        return UnaryOpField(this, { i -> i.cos() })
    }

    override fun acos(): IField<DATATYPE> {
        return UnaryOpField(this, { i -> i.acos() })
    }

    override fun pow(exponent: Double): IField<DATATYPE> {
        return DoubleOpField(this, exponent, { i, j -> i.pow(j) })
    }

    override fun pow(exponent: IField<DATATYPE>): IField<DATATYPE> {
        return BinaryOpField(this, exponent, { i, j -> i.pow(j) })
    }

    override fun log(): IField<DATATYPE> {
        return UnaryOpField(this, { i -> i.log() })
    }

    override fun exp(): IField<DATATYPE> {
        return UnaryOpField(this, { i -> i.exp() })
    }

    fun dx(): Double {
//        return getDomain().DXA
        return domain.DX
    }

    fun dy(): Double {
//        return getDomain().DY
        return domain.DY
    }

    fun calculate(): Array2D<DATATYPE> {
        return Array2D(iSize(), jSize(), { i, j -> get(i, j) })
    }

//    fun getDomain(): Domain<DATATYPE>

    fun sum(): DATATYPE {
        var total = this[0, 0]
        for (i in 0 until this.iSize()) {
            for (j in 0 until this.jSize()) {
                total += this[i, j]
            }
        }
        total -= this[0, 0]
        return total
    }

}