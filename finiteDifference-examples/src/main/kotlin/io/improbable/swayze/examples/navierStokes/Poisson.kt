package io.improbable.swayze.examples.navierStokes

import gnuPlotLib.PlotField
import io.improbable.swayze.finiteDifference.*
import io.improbable.keanu.kotlin.ArithmeticDouble

class Poisson (val params: FieldParams, val source: Array2D<ArithmeticDouble>) {

    var domain = Domain(params.DX, params.DY,
                        Boundary.Dirichlet(ArithmeticDouble(0.0)),
                        Boundary.Dirichlet(ArithmeticDouble(0.0)),
                        Boundary.Dirichlet(ArithmeticDouble(0.0)),
                        Boundary.Dirichlet(ArithmeticDouble(0.0)))

    var p = Field(params.XSIZE, params.YSIZE, domain.clone(), { _, _ -> ArithmeticDouble(0.0) })

    // Pre-compute this for efficiency.
    var sourceTerm = Field(params.XSIZE, params.YSIZE, domain.clone(), {i, j ->
        (source[i, j] * params.DX * params.DX * params.DY * params.DY) / (2.0 * (params.DX * params.DX + params.DY * params.DY))
    })

    var plot = PlotField()


    fun solve() {
        while (params.iteration < params.maxIteration) {
            params.iteration += 1
            p = Field(Laplacian(p) - sourceTerm)
            housekeeping()
        }
    }

    fun housekeeping() {
        println("it = " + params.iteration)
        plot.linePlot(plot.scalarToGNUplotMatrix(p, p.iSize(), p.jSize()))
    }
}

fun main(args: Array<String>) {

    // Define the params
    val XSIZE = 50
    val YSIZE = 50
    val DX = 2.0 / (XSIZE - 1.0)
    val DY = 1.0 / (YSIZE - 1.0)
    val maxIteration = 100

    // Set the source to be 100 at i=nx/4,j=ny/4, −100  at i=nx∗3/4,j=3/4ny and 0 otherwise
    val source = Array2D(XSIZE, YSIZE, {i, j ->
        if (i == XSIZE/4 && j == YSIZE/4) {
            ArithmeticDouble(100.0)
        } else if (i == 3*XSIZE/4 && j == 3*XSIZE/4) {
            ArithmeticDouble(-100.0)
        } else {
            ArithmeticDouble(0.0)
        }
    })

    var params = FieldParams(XSIZE, YSIZE, DX, DY)
    params.maxIteration = maxIteration

    var problem = Poisson(params, source)
    problem.solve()
}