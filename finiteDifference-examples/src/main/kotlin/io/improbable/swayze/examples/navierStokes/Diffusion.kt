package io.improbable.swayze.examples.navierStokes

import gnuPlotLib.PlotField
import io.improbable.swayze.finiteDifference.*
import io.improbable.keanu.kotlin.ArithmeticDouble

/**
 * This example demonstrates 2D diffusion in isolation. We begin with a square 'hat' function and diffuse this out over
 * currentTime.
 */
class Diffusion (var params: FieldParams, var nu: Double) {

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

    var plot = PlotField()


    fun solve() {
        while (params.t < params.TLENGTH) {
            params.t += params.DT
            timestep()
            housekeeping()
        }
    }


    fun timestep() {
        // Compute the temporal difference
        val Du = (d2_dx2(u) + d2_dy2(u)) * nu * params.DT

        // Increment the prognostic variable
        u += Du.calculate()
    }


    fun housekeeping() {
        println("t = " + params.t)
        plot.linePlot(plot.scalarToGNUplotMatrix(u))
    }
}

fun main(args: Array<String>) {

    // Define the params
    val XSIZE = 31
    val YSIZE = 31
    val sigma = 0.25
    val nu = 0.05
    val numberTimesteps = 100
    val DX = 2.0 / (XSIZE - 1.0)
    val DY = 2.0 / (YSIZE - 1.0)
    val DT = sigma * DX * DY / nu
    val TLENGTH = DT * numberTimesteps

    var params = FieldParams(XSIZE, YSIZE, TLENGTH, DX, DY, DT)

    var problem = Diffusion(params, nu)
    problem.solve()
}