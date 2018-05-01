package io.improbable.swayze.examples.navierStokes

import gnuPlotLib.PlotField
import io.improbable.swayze.finiteDifference.*
import io.improbable.keanu.kotlin.ArithmeticDouble

/**
 * In this example we solve the Laplace equation in 2D - physicist may recognise this as the steady-state heat equation.
 * We are looking to see the field 'p' iterate to accommodate our boundary forcing.
 */
class Laplace (val params: FieldParams) {

    var domain = Domain(params.DX, params.DY,
                        Boundary.Dirichlet(ArithmeticDouble(0.0)),
                        Boundary.Dirichlet<ArithmeticDouble>( { i:Int -> ArithmeticDouble(i * params.DX)} ),
                        Boundary.Neumann(ArithmeticDouble(0.0), params.DX),
                        Boundary.Neumann(ArithmeticDouble(0.0), params.DX))

    var p = Field(params.XSIZE, params.YSIZE, domain.clone(), { _, _ -> ArithmeticDouble(0.0) })

    var plot = PlotField()


    fun solve() {
        while (params.currentError > params.targetError) {
            params.iteration += 1
            var np = Laplacian(p)
            params.currentError = L1Norm(np, p)
            p = Field(np)
            housekeeping()
        }
    }


    fun housekeeping() {
        println("it = " + params.iteration + " \ttarget error: " + params.targetError + " \tcurrent error: " +
                params.currentError + " \tstill to go: " + (params.currentError - params.targetError))
        plot.linePlot(plot.scalarToGNUplotMatrix(p, p.iSize(), p.jSize()))
    }
}


fun main(args: Array<String>) {

    // Define the params
    val XSIZE = 31
    val YSIZE = 31
    val DX = 2.0 / (XSIZE - 1.0)
    val DY = 1.0 / (YSIZE - 1.0)
    val targetError = 1e-4

    var params = FieldParams(XSIZE, YSIZE, DX, DY, targetError)

    var problem = Laplace(params)
    problem.solve()
}