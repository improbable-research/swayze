package io.improbable.swayze.finiteDifference

import io.improbable.keanu.kotlin.ArithmeticDouble

fun L1Norm(newF: IField<ArithmeticDouble>, oldF: Field<ArithmeticDouble>): Double {
    var abs_error = Field(newF.iSize(), newF.jSize(), newF.domain.clone(), { i, j ->
        var error = newF[i, j].value - oldF[i, j].value
        ArithmeticDouble(Math.sqrt(error * error))
    })
    return (abs_error.sum() / oldF.sum()).value
}

fun dot(u: IField<ArithmeticDouble>, v: IField<ArithmeticDouble>): Field<ArithmeticDouble> {
    return Field(u.iSize(), u.jSize(), u.domain.clone(), { i, j ->
        ArithmeticDouble(Math.sqrt(u[i, j].value * u[i, j].value + v[i, j].value * v[i, j].value))
    })
}

