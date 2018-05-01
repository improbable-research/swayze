package io.improbable.swayze.examples.navierStokes

import io.improbable.swayze.finiteDifference.Boundary
import io.improbable.swayze.finiteDifference.Domain
import io.improbable.keanu.kotlin.ArithmeticDouble

class FieldParams(var XSIZE: Int, var YSIZE: Int, var DX: Double, var DY: Double) {

    var DT = 0.0

    var TLENGTH = 0.0
    var maxIteration = 0
    var targetError = 0.0
    var numberTimesteps = 0

    var t = 0.0
    var iteration = 0
    var innerIteration = 0
    var currentError = Double.MAX_VALUE

    var dumpFrequency = 0

    // Physical constants
    var nu = 0.1
    var rho = 1.0
    var c = 1.0

    // Ones domain - for building obstructions
    var onesDomain = Domain(DX, DY,
            Boundary.Dirichlet(ArithmeticDouble(1.0)),
            Boundary.Dirichlet(ArithmeticDouble(1.0)),
            Boundary.Dirichlet(ArithmeticDouble(1.0)),
            Boundary.Dirichlet(ArithmeticDouble(1.0)))

    constructor(XSIZE: Int, YSIZE: Int, TLENGTH: Double, DX: Double, DY: Double, DT: Double):
            this(XSIZE, YSIZE, DX, DY) {
        this.DT = DT
        this.TLENGTH = TLENGTH
    }

    constructor(XSIZE: Int, YSIZE: Int, DX: Double, DY: Double, targetError: Double):
            this(XSIZE, YSIZE, DX, DY) {
        this.targetError = targetError
    }
}
