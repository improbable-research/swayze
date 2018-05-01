package io.improbable.swayze.examples.navierStokes

import io.improbable.swayze.finiteDifference.*
import io.improbable.keanu.kotlin.ArithmeticDouble

/**
 * A classic fluid-dynamics problem, the lid-driven cavity flow problem imagines a situation where a main flow - e.g. a
 * river passes a notch in the bank. The shear effects from the passing flow cause a gyre to form in the notch. Here we
 * model the notch as square in shape and represent the passing river flow as a velocity boundary condition on the top
 * boundary, parallel to it (note the 'lidVelocity' term on uDomain). This idealisation has applications in river mixing
 * problems - e.g. to study the residence of a pollutant.
 */
fun main(args: Array<String>) {

    // Physical fluid properties
    val rho = 1.0
    val nu = 0.1

    // Forcing
    val lidVelocity = 1.0

    // Domain spatial discretisation
    val XSIZE = 41
    val YSIZE = 41
    val DX = 2.0 / (XSIZE - 1.0)
    val DY = 2.0 / (YSIZE - 1.0)

    // Temporal discretisation
    val numberTimesteps = 700
    val DT = 0.001
    val TLENGTH = numberTimesteps * DT
    val poissonIterations = 50

    // Load up the parameters object
    var params = FieldParams(XSIZE, YSIZE, TLENGTH, DX, DY, DT)
    params.maxIteration = poissonIterations
    params.numberTimesteps = numberTimesteps
    params.rho = rho
    params.nu = nu

    // Define the domain and boundary conditions for velocity and pressure
    var uDomain = Domain(params.DX, params.DY,
                        Boundary.Dirichlet(ArithmeticDouble(lidVelocity)),
                        Boundary.Dirichlet(ArithmeticDouble(0.0)),
                        Boundary.Dirichlet(ArithmeticDouble(0.0)),
                        Boundary.Dirichlet(ArithmeticDouble(0.0)))
    var vDomain = Domain(params.DX, params.DY,
                        Boundary.Dirichlet(ArithmeticDouble(0.0)),
                        Boundary.Dirichlet(ArithmeticDouble(0.0)),
                        Boundary.Dirichlet(ArithmeticDouble(0.0)),
                        Boundary.Dirichlet(ArithmeticDouble(0.0)))
    var pDomain = Domain(params.DX, params.DY,
                        Boundary.Dirichlet(ArithmeticDouble(0.0)),
                        Boundary.Neumann(ArithmeticDouble(0.0)),
                        Boundary.Neumann(ArithmeticDouble(0.0)),
                        Boundary.Neumann(ArithmeticDouble(0.0)))

    // Set the initial conditions for the velocity and pressure
    var u = Field(params.XSIZE, params.YSIZE, uDomain, { _, _ -> ArithmeticDouble(0.0) })
    var v = Field(params.XSIZE, params.YSIZE, vDomain, { _, _ -> ArithmeticDouble(0.0) })
    var p = Field(params.XSIZE, params.YSIZE, pDomain, { _, _ -> ArithmeticDouble(0.0) })

    var solver = NavierStokesSolver(u, v, p, params)

    solver.solve()

}