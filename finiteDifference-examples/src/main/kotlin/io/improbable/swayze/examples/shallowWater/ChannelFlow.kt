package io.improbable.swayze.examples.shallowWater

import io.improbable.keanu.kotlin.ArithmeticDouble
import io.improbable.swayze.examples.navierStokes.FieldParams
import io.improbable.swayze.finiteDifference.Boundary
import io.improbable.swayze.finiteDifference.Domain
import io.improbable.swayze.finiteDifference.Field
import java.lang.Math.sin

fun main(args: Array<String>) {

    // Physical fluid properties
    val nu = 3.0

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
    params.nu = nu

    // Define the domain and boundary conditions fot velocity and pressure
    val velocityDomain = Domain(params.DX, params.DY,
            Boundary.Dirichlet(ArithmeticDouble(0.0)),
            Boundary.Dirichlet(ArithmeticDouble(0.0)),
            Boundary.Periodic(),
            Boundary.Periodic())
    val freeSurfaceDomain = Domain(params.DX, params.DY,
            Boundary.Neumann(ArithmeticDouble(0.0)),
            Boundary.Neumann(ArithmeticDouble(0.0)),
            Boundary.Periodic(),
            Boundary.Periodic())

    // Set the initial conditions for velocity and free-surface height
    val u = Field(params.XSIZE, params.YSIZE, velocityDomain, { _, _ -> ArithmeticDouble(0.01)})
    val v = Field(params.XSIZE, params.YSIZE, velocityDomain, { _, _ -> ArithmeticDouble(0.0) })
    val eta = Field(params.XSIZE, params.YSIZE, freeSurfaceDomain, { i, _ ->
        ArithmeticDouble(0.1 * sin((2.0 * 3.1415) / XSIZE * i))
    })

    val solver = ShallowWaterSolver(u, v, eta, params)

    solver.solve()
}