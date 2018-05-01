package io.improbable.swayze.examples.navierStokes

import gnuPlotLib.PlotField
import io.improbable.swayze.finiteDifference.*
import io.improbable.keanu.kotlin.ArithmeticDouble

/**
 * In this example we illustrate simple linear convection of a hat function in u. Note the numerical diffusion that
 * occurs, rounding the sharp definition of the initial hat function.
 */
class LinearConvection (val params: FieldParams, val c: Double) {

    var domain = Domain(params.DX, params.DY,
            Boundary.Periodic<ArithmeticDouble>(),
            Boundary.Periodic(),
            Boundary.Periodic(),
            Boundary.Periodic())

    var u = Field(params.XSIZE, params.YSIZE, domain.clone(), { i, j ->
        if (i > params.XSIZE/4.0 && i < params.XSIZE/2.0 &&
                j > params.YSIZE/4.0 && j < params.YSIZE/2.0) {
            ArithmeticDouble(2.0)
        } else {
            ArithmeticDouble(1.0)
        }
    })

    var plot = PlotField()


    fun solve() {
        housekeeping()
        while (params.t < params.TLENGTH) {
            params.t += params.DT
            timestep()
            housekeeping()
        }
    }


    fun timestep() {
        // Compute the temporal difference
        val Du = (bd_dx(u) + bd_dy(u)) * params.DT * (- c)

        // Increment the prognostic variable
        u += Du.calculate()
    }


    fun housekeeping() {
        println("t = " + params.t)
        plot.linePlot(plot.scalarToGNUplotMatrix(u, u.iSize(), u.jSize()))
    }
}


fun main(args: Array<String>) {

    // Define the params
    val XSIZE = 81
    val YSIZE = 81
    val c = 1.0
    val sigma = 0.2
    val numberTimesteps = 200
    val DX = 2.0 / (XSIZE - 1.0)
    val DY = 2.0 / (YSIZE - 1.0)
    val DT = sigma * DX
    val TLENGTH = DT * numberTimesteps

    var params = FieldParams(XSIZE, YSIZE, TLENGTH, DX, DY, DT)

    var problem = LinearConvection(params, c)
    problem.solve()
}
