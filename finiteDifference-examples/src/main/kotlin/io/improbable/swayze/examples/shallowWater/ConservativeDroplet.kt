package io.improbable.swayze.examples.shallowWater

import gnuPlotLib.PlotField
import io.improbable.keanu.kotlin.ArithmeticDouble
import io.improbable.swayze.finiteDifference.*
import kotlin.math.pow

class ConservativeDroplet (domainSize: Double, noElements: Int) {

    var XSIZE = noElements
    var YSIZE = noElements
    var dx = domainSize / noElements
    var dy = dx
    var dt = dx / 100.0

    var xDroplet = 0.5 * domainSize
    var yDroplet = 0.5 * domainSize
    var radiusDroplet = domainSize / (75.0 * noElements)

    var g = 1.0
    var H = 0.0

    var plot = PlotField()

    var currentTime = 0.0
    var endTime = 30.0

    var velocityDomain = Domain(dx, dy,
            Boundary.Dirichlet(ArithmeticDouble(0.0)),
            Boundary.Dirichlet(ArithmeticDouble(0.0)),
            Boundary.Dirichlet(ArithmeticDouble(0.0)),
            Boundary.Dirichlet(ArithmeticDouble(0.0)))
    var freeSurfaceDomain = Domain(dx,dy,
            Boundary.Neumann(ArithmeticDouble(0.0)),
            Boundary.Neumann(ArithmeticDouble(0.0)),
            Boundary.Neumann(ArithmeticDouble(0.0)),
            Boundary.Neumann(ArithmeticDouble(0.0)))

    var u = Field(XSIZE, YSIZE, velocityDomain, { _, _ -> ArithmeticDouble(0.0) })
    var v = Field(XSIZE, YSIZE, velocityDomain, { _, _ -> ArithmeticDouble(0.0) })
    var eta = Field(XSIZE, YSIZE, freeSurfaceDomain, { i, j ->
        if ((i*dx - xDroplet).pow(2) + (j*dy - yDroplet).pow(2) < radiusDroplet) {
            ArithmeticDouble(1.1)
        } else {
            ArithmeticDouble(1.0)
        }})

    fun solve () {
        while (currentTime < endTime) {
            println("t = " + currentTime)
            timestep()
            currentTime += dt
            plot.linePlot(plot.scalarToGNUplotMatrix(eta))
        }
    }

    fun timestep() {
        var deta_dt = -d_dx(u * (eta)) - d_dy(v * (eta))
        var du_dt = (deta_dt * u
                     - d_dx(eta * u * u)
                     - d_dx(eta * eta * g * 0.5)
                     - d_dy(eta * u * v)
                     ) / eta

        var dv_dt = (deta_dt * v
                     - d_dy(eta * v * v)
                     - d_dy(eta * eta * g * 0.5)
                     - d_dx(eta * u * v)
                     ) / eta

        eta = Field(eta + deta_dt * dt)
        u = Field(u + du_dt * dt)
        v = Field(v + dv_dt * dt)
    }
}

fun main(args: Array<String>) {

    var domainSize = 0.25
    var noElements = (domainSize * 100).toInt()

    var solver = ConservativeDroplet(domainSize, noElements)

    solver.solve()
}
