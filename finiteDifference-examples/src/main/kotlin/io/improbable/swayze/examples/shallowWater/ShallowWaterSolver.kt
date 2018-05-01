package io.improbable.swayze.examples.shallowWater

import gnuPlotLib.PlotField
import io.improbable.keanu.kotlin.ArithmeticDouble
import io.improbable.swayze.examples.navierStokes.FieldParams
import io.improbable.swayze.finiteDifference.*

class ShallowWaterSolver (uInitial: Field<ArithmeticDouble>,
                          vInitial: Field<ArithmeticDouble>,
                          etaInitial: Field<ArithmeticDouble>,
                          var params: FieldParams) {

    var u = uInitial
    var v = vInitial
    var eta = etaInitial
    var velocityMagnitude = velocityMagnitude()

    // Physical conditions
    var h = Field(params.XSIZE, params.YSIZE, params.onesDomain, { _, _ -> ArithmeticDouble(50.0) })   // Average depth
    var c_b = Field(params.XSIZE, params.YSIZE, params.onesDomain, { _,_ -> ArithmeticDouble(0.0025) }) // Bottom friction
    var g = 9.81    // Acceleration due to gravity
    var H = h + eta

    var plot = PlotField()

    fun solve() {
        while (params.t < params.TLENGTH) {
            params.t += params.DT; params.iteration += 1
            timestep()
            housekeeping()
        }
    }

    fun timestep() {
        // Update some handy quantities for efficiency
        velocityMagnitude = velocityMagnitude()
        H = h + eta

        // Solve velocity and free-surface height
        u = solveXDirectionVelocityComponent(u)
        v = solveYDirectionVelocityComponent(v)
        eta = solveFreeSurface(eta)
    }

    fun velocityMagnitude(): Field<ArithmeticDouble> {
        return Field((u * u + v * v).pow(0.5))
    }

    fun nonEtaVelocityTerms(F: Field<ArithmeticDouble>): Field<ArithmeticDouble> {
        var dt = params.DT
        return Field(F - u * bd_dx(F) * dt
                             - v * bd_dy(F) * dt
                             + d2_dx2(F) * params.nu * dt
                             + d2_dy2(F) * params.nu * dt
                             - c_b / H * velocityMagnitude * F * dt)
    }

    fun solveXDirectionVelocityComponent(F: Field<ArithmeticDouble>): Field<ArithmeticDouble> {
        var dt = params.DT
        return Field(nonEtaVelocityTerms(F) - d_dx(eta) * g * dt)
    }

    fun solveYDirectionVelocityComponent(F: Field<ArithmeticDouble>): Field<ArithmeticDouble> {
        var dt = params.DT
        return Field(nonEtaVelocityTerms(F) - d_dy(eta) * g * dt)
    }

    fun solveFreeSurface(eta: Field<ArithmeticDouble>): Field<ArithmeticDouble> {
        var dt = params.DT
        return Field(eta - d_dx(H * u) * dt
                               - d_dy(H * v) * dt)
    }

    fun housekeeping() {
        println("t = " + params.t)
        plot.linePlot(plot.scalarToGNUplotMatrix(eta))
    }
}