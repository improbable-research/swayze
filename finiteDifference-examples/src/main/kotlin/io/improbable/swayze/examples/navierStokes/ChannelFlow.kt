package io.improbable.swayze.examples.navierStokes

import io.improbable.swayze.finiteDifference.*
import io.improbable.keanu.kotlin.ArithmeticDouble

/**
 * One of the simplest possible examples of 2D Navier Stokes, this example demonstrates straightforward flow down a
 * rectangular channel.
 */
fun main(args: Array<String>) {

    // Physical fluid properties
    val rho = 1.0
    val nu = 0.1

    // Forcing given by a source term on velocity's x component
    // this kind of mimics effect of pressure-driven channel
    val xDirectionVelocitySourceTerm = 1.0

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

    var params = FieldParams(XSIZE, YSIZE, TLENGTH, DX, DY, DT)
    params.maxIteration = poissonIterations
    params.numberTimesteps = numberTimesteps
    params.rho = rho
    params.nu = nu

    // Define the domain and boundary conditions fot velocity and pressure
    var velocityDomain = Domain(params.DX, params.DY,
            Boundary.Dirichlet(ArithmeticDouble(0.0)),
            Boundary.Dirichlet(ArithmeticDouble(0.0)),
            Boundary.Periodic(),
            Boundary.Periodic())
    var pressureDomain = Domain(params.DX, params.DY,
            Boundary.Neumann(ArithmeticDouble(0.0)),
            Boundary.Neumann(ArithmeticDouble(0.0)),
            Boundary.Periodic(),
            Boundary.Periodic())

    // Set the initial conditions for velocity and pressure
    var u = Field(params.XSIZE, params.YSIZE, velocityDomain, { _, _ -> ArithmeticDouble(0.0) })
    var v = Field(params.XSIZE, params.YSIZE, velocityDomain, { _, _ -> ArithmeticDouble(0.0) })
    var p = Field(params.XSIZE, params.YSIZE, pressureDomain, { _, _ -> ArithmeticDouble(0.0) })

    var solver = NavierStokesSolver(u, v, p, params)

    // Don't forget to add the forcing
    solver.xDirectionVelocitySourceTerm = xDirectionVelocitySourceTerm

    solver.solve()
}