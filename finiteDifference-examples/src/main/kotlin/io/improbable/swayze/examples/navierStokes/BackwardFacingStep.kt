package io.improbable.swayze.examples.navierStokes

import io.improbable.swayze.finiteDifference.*
import io.improbable.keanu.kotlin.ArithmeticDouble

/**
 * In this example we implement a variant on the classic backwards facing step problem where we see the shear layer
 * between moving and stationary fluid.
 * Here we use a rectangular domain and split the inlet channel-direction boundary condition such that half the domain
 * is effectively closed (0 Dirichlet) and the other half has an inlet of 1 m/s.
 */
fun main(args: Array<String>) {

    // Physical fluid properties
    val rho = 1.0
    val nu = 0.10

    // Domain spatial discretisation
    val XSIZE = 41
    val YSIZE = 41
    val DX = 2.0 / (XSIZE - 1.0)
    val DY = 2.0 / (YSIZE - 1.0)

    // Temporal discretisation
    val numberTimesteps = 500
    val DT = 0.001
    val TLENGTH = numberTimesteps * DT
    val poissonIterations = 50

    // Set the parameters object
    var params = FieldParams(XSIZE, YSIZE, TLENGTH, DX, DY, DT)
    params.maxIteration = poissonIterations
    params.numberTimesteps = numberTimesteps
    params.rho = rho
    params.nu = nu

    // Define the domain and boundary conditions for velocity and pressure
    var xComponentVelocityDomain = Domain(params.DX, params.DY,
            Boundary.Dirichlet(ArithmeticDouble(0.0)),
            Boundary.Dirichlet(ArithmeticDouble(0.0)),
            Boundary.Neumann(ArithmeticDouble(0.0)),
            Boundary.Dirichlet<ArithmeticDouble>( {i ->
                if (i > params.XSIZE/2) {
                    ArithmeticDouble(1.0)
                } else {
                    ArithmeticDouble(0.0)
                }
            }))
    var yComponentVelocityDomain = Domain(params.DX, params.DY,
            Boundary.Dirichlet(ArithmeticDouble(0.0)),
            Boundary.Dirichlet(ArithmeticDouble(0.0)),
            Boundary.Dirichlet(ArithmeticDouble(0.0)),
            Boundary.Dirichlet(ArithmeticDouble(0.0)))
    var pressureDomain = Domain(params.DX, params.DY,
            Boundary.Neumann(ArithmeticDouble(0.0)),
            Boundary.Neumann(ArithmeticDouble(0.0)),
            Boundary.Neumann(ArithmeticDouble(0.0)),
            Boundary.Neumann(ArithmeticDouble(0.0)))

    // Initialise the fields and set the initial conditions
    var u = Field(params.XSIZE, params.YSIZE, xComponentVelocityDomain, { _, _ -> ArithmeticDouble(0.0) })
    var v = Field(params.XSIZE, params.YSIZE, yComponentVelocityDomain, { _, _ -> ArithmeticDouble(0.0) })
    var p = Field(params.XSIZE, params.YSIZE, pressureDomain, { _, _ -> ArithmeticDouble(0.0) })

    var solver = NavierStokesSolver(u, v, p, params)

    solver.solve()
}