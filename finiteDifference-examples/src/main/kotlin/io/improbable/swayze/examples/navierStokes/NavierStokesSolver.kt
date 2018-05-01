package io.improbable.swayze.examples.navierStokes

import io.improbable.swayze.finiteDifference.*
import gnuPlotLib.PlotField
import io.improbable.keanu.kotlin.ArithmeticDouble

class NavierStokesSolver (uInitial: Field<ArithmeticDouble>, vInitial: Field<ArithmeticDouble>, pInitial: Field<ArithmeticDouble>,
                          var params: FieldParams) {

    var u = uInitial
    var v = vInitial
    var p = pInitial

    var xDirectionVelocitySourceTerm = 0.0
    var yDirectionVelocitySourceTerm = 0.0

    lateinit var obstruction: Field<ArithmeticDouble>

    var plot = PlotField()

    constructor(uInitial: Field<ArithmeticDouble>, vInitial: Field<ArithmeticDouble>, pInitial: Field<ArithmeticDouble>,
                params: FieldParams, obstruction: Field<ArithmeticDouble>) : this(uInitial, vInitial, pInitial, params) {
        this.obstruction = obstruction
    }


    fun solve() {
        while (params.t < params.TLENGTH) {
            params.t += params.DT; params.iteration += 1
            timestep()
            housekeeping()
        }
    }


    fun timestep() {
        solvePoissonPressureEquation()
        u = solveXDirectionVelocityComponent(u)
        v = solveYDirectionVelocityComponent(v)
    }


    fun nonPressureVelocityTerms(F: Field<ArithmeticDouble>): Field<ArithmeticDouble> {
        var dt = params.DT
        return Field(F - u * bd_dx(F) * dt
                             - v * bd_dy(F) * dt
                             + d2_dx2(F) * params.nu * dt
                             + d2_dy2(F) * params.nu * dt)
    }


    fun solveXDirectionVelocityComponent(F: Field<ArithmeticDouble>): Field<ArithmeticDouble> {
        var dt = params.DT
        return Field(nonPressureVelocityTerms(F)
                           - d_dx(p) * dt / params.rho
                           + xDirectionVelocitySourceTerm * dt)
    }


    fun solveYDirectionVelocityComponent(F: Field<ArithmeticDouble>) : Field<ArithmeticDouble> {
        var dt = params.DT
        return Field(nonPressureVelocityTerms(F)
                           - d_dy(p) * dt / params.rho
                           + yDirectionVelocitySourceTerm * dt)
    }


    fun pressureSource(): Field<ArithmeticDouble> {
        var dt = params.DT
        return Field((d_dx(u) + d_dy(v)) * (1.0/dt)
                            - d_dx(u) * d_dx(u)
                            - d_dy(u) * d_dx(v) * 2.0
                            - d_dy(v) * d_dy(v))
    }


    fun solvePoissonPressureEquation() {
        params.innerIteration = 0
        var sourceTerm = pressureSource() * (p.dx()*p.dx()*p.dy()*p.dy())/(2*(p.dx()*p.dx() + p.dy()*p.dy()))
        while (params.innerIteration < params.maxIteration) {
            params.innerIteration += 1
            p = Field(Laplacian(p) -  sourceTerm)
        }
    }


    fun housekeeping() {
        println("t = " + params.t)
        plot.linePlot(plot.scalarToGNUplotMatrix(dot(u, v)))
    }


}