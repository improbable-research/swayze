package io.improbable.swayze.finiteDifference

import io.improbable.keanu.kotlin.ArithmeticDouble
import kotlin.math.PI
import kotlin.math.cos

fun makeUnitSquareOnesField(n: Int) : Field<ArithmeticDouble> {
    var domain = Domain(1.0 / n,
            1.0 / n,
            Boundary.Dirichlet(ArithmeticDouble(1.0)),
            Boundary.Dirichlet(ArithmeticDouble(1.0)),
            Boundary.Dirichlet(ArithmeticDouble(1.0)),
            Boundary.Dirichlet(ArithmeticDouble(1.0)))
    var f = Field(n, n, domain, { _, _ -> ArithmeticDouble(1.0) })
    return f
}


fun makePeriodicField(n: Int): Field<ArithmeticDouble> {
    var domain = Domain<ArithmeticDouble>(1.0 / (n - 1),
            1.0 / (n - 1),
            Boundary.Periodic(),
            Boundary.Periodic(),
            Boundary.Periodic(),
            Boundary.Periodic())
    var f = Field(n, n, domain, { i, j -> ArithmeticDouble(cos(PI * i * domain.DX) * cos(PI * j * domain.DY)) })
    return f
}


fun makeUniqueValuedField(n: Int): Field<ArithmeticDouble> {
    var domain = Domain<ArithmeticDouble>(1.0 / (n - 1),
            1.0 / (n - 1),
            Boundary.Periodic(),
            Boundary.Periodic(),
            Boundary.Periodic(),
            Boundary.Periodic())
    var f = Field(n, n, domain, { i, j -> ArithmeticDouble(i * 100.0 + j) })
    return f
}


fun makeMixedBoundaryField(n: Int): Field<ArithmeticDouble> {
    var domain = Domain<ArithmeticDouble>(1.0 / (n - 1),
            1.0 / (n - 1),
            Boundary.Dirichlet(ArithmeticDouble(0.0)),
            Boundary.Dirichlet(ArithmeticDouble(0.0)),
            Boundary.Periodic(),
            Boundary.Periodic())
    var f = Field(n, n, domain, { i, j -> ArithmeticDouble(i * 100.0 + j) })
    return f
}

