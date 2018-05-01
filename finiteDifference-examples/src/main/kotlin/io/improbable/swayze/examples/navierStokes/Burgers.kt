package io.improbable.swayze.examples.navierStokes

import gnuPlotLib.*
import io.improbable.swayze.finiteDifference.*
import io.improbable.keanu.kotlin.ArithmeticDouble

/**
 * In this example we implement and solve the Burgers' equation - handy because it contains the full convective
 * nonlinearity of the flow equations
 */
class Burgers (var params: FieldParams) {

    var domain = Domain(params.DX, params.DY,
            Boundary.Dirichlet(ArithmeticDouble(1.0)),
            Boundary.Dirichlet(ArithmeticDouble(1.0)),
            Boundary.Dirichlet(ArithmeticDouble(1.0)),
            Boundary.Dirichlet(ArithmeticDouble(1.0)))

    var u = Field(params.XSIZE, params.YSIZE, domain.clone(), { i, j ->
        if (i > params.XSIZE/4.0 && i < params.XSIZE/2.0 &&
                j > params.YSIZE/4.0 && j < params.YSIZE/2.0) {
            ArithmeticDouble(2.0)
        } else {
            ArithmeticDouble(1.0)
        }
    })

    var v = Field(params.XSIZE, params.YSIZE, domain.clone(), { i, j -> u[i, j] })

    var plot = PlotField()


    fun solve() {
        println("Completed initialisation, beginning solve")
        while (params.t < params.TLENGTH) {
            params.t += params.DT
            timestep()
            housekeeping()
        }
    }


    fun timestep() {
        // Compute the temporal difference
        val Du = (((u * bd_dx(u) + v * bd_dy(u)) * -params.c) + ((d2_dx2(u) + d2_dy2(u)) * params.nu)) * params.DT
        val Dv = (((u * bd_dx(v) + v * bd_dy(v)) * -params.c) + ((d2_dx2(v) + d2_dy2(v)) * params.nu)) * params.DT

        // Increment the prognostic variable
        u += Du.calculate()
        v += Dv.calculate()
    }


    fun housekeeping() {
        println("t = " + params.t)
        plot.linePlot(plot.scalarToGNUplotMatrix(dot(u, v)))
    }
}

fun main(args: Array<String>) {

    // Define the params
    val XSIZE = 101
    val YSIZE = 101
    val nu = 0.01
    val c = 1.0
    val sigma = 0.09
    val numberTimesteps = 120
    val DX = 2.0 / (XSIZE - 1.0)
    val DY = 2.0 / (YSIZE - 1.0)
    val DT = sigma * DX
    val TLENGTH = DT * numberTimesteps

    var params = FieldParams(XSIZE, YSIZE, TLENGTH, DX, DY, DT)
    params.nu = nu
    params.c = c

    var problem = Burgers(params)
    problem.solve()
}
