package probabilisticSolvers

import io.improbable.swayze.finiteDifference.*
import fourDVar.IModel
import gnuPlotLib.PlotField
import io.improbable.keanu.kotlin.DoubleOperators
import io.improbable.keanu.randomFactory.RandomFactory
import io.improbable.keanu.vertices.dbl.probabilistic.GaussianVertex

class NavierStokesProbabilisticSolver<DOUBLE: DoubleOperators<DOUBLE>> (uInitial: Field<DOUBLE>, vInitial: Field<DOUBLE>, pInitial: Field<DOUBLE>,
                                                                        var params: FieldParams, var random: RandomFactory<DOUBLE>): IModel<DOUBLE> {

    val TIMESTEPS_PER_WINDOW = 5

    var u = uInitial
    var v = vInitial
    var p = pInitial

    var xDirectionVelocitySourceTerm = 0.0
    var yDirectionVelocitySourceTerm = 0.0

    lateinit var xMask: Field<DOUBLE>
    lateinit var yMask: Field<DOUBLE>

    var plot = PlotField()


    fun solve() {
        //makeMasks()
        while (params.t < params.TLENGTH) {
            params.t += params.DT; params.iteration += 1
            step()
            housekeeping()
        }
    }


    override fun runWindow(): Collection<DOUBLE> {
        val observations = ArrayList<DOUBLE>(TIMESTEPS_PER_WINDOW)
        for (i in 1..TIMESTEPS_PER_WINDOW) {
            step()
            observations.add(observe())
            println("Iteration: " + i)
        }
        return observations
    }


    override fun step() {
        solvePoissonPressureEquation()
        u = solveXDirectionVelocityComponent(u)
        v = solveYDirectionVelocityComponent(v)
    }


    fun nonPressureVelocityTerms(F: Field<DOUBLE>): Field<DOUBLE> {
        var dt = params.DT
        return Field(F - u * bd_dx(F) * xMask * dt
                - v * bd_dy(F) * yMask * dt
                + d2_dx2(F) * xMask * params.nu * dt
                + d2_dy2(F) * xMask * params.nu * dt)
    }


    fun solveXDirectionVelocityComponent(F: Field<DOUBLE>): Field<DOUBLE> {
        var dt = params.DT
        return Field(nonPressureVelocityTerms(F) * xMask * yMask
                           - d_dx(p) * xMask * dt / params.rho
                           + xMask * xDirectionVelocitySourceTerm * dt)
    }


    fun solveYDirectionVelocityComponent(F: Field<DOUBLE>) : Field<DOUBLE> {
        var dt = params.DT
        return Field(nonPressureVelocityTerms(F) * xMask * yMask
                           - d_dy(p) * yMask * dt / params.rho
                           + yMask * yDirectionVelocitySourceTerm * dt)
    }


    fun pressureSource(): Field<DOUBLE> {
        var dt = params.DT
        return Field((d_dx(u) + d_dy(v)) * xMask * yMask * (1.0/dt)
                            - d_dx(u) * d_dx(u) * xMask
                            - d_dy(u) * d_dx(v) * xMask * yMask * 2.0
                            - d_dy(v) * d_dy(v) * yMask)
    }


    fun solvePoissonPressureEquation() {
        params.innerIteration = 0
        var sourceTerm = pressureSource() * (p.dx()*p.dx()*p.dy()*p.dy())/(2*(p.dx()*p.dx() + p.dy()*p.dy()))
        while (params.innerIteration < params.maxIteration) {
            params.innerIteration += 1
            p = Field(Laplacian(p) -  sourceTerm)  // TODO this is super ugly
        }
    }


    private fun observe(): DOUBLE {
        val pressure = p[10,10]
        return random.nextGaussian()
    }

    override fun getState(): Collection<DOUBLE> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setState(state: Collection<DOUBLE>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    fun housekeeping() {
        println("t = " + params.t)
        //plot.linePlot(plot.scalarToGNUplotMatrix(dot(u, v)))
    }


}