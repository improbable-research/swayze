package rayleighBenard

import io.improbable.swayze.finiteDifference.*
import fourDVar.IModel
import io.improbable.keanu.kotlin.DoubleOperators
import temporary.RandomFactory


class PhysicalConvection<DOUBLE : DoubleOperators<DOUBLE>>(startState: Collection<DOUBLE>,
                                                           val domain: Domain<DOUBLE>, val random: RandomFactory<DOUBLE>):
        IModel<DOUBLE> {

    var t = 0.0
    var TLENGTH = 0.001
    var DT = 0.00001
    var XSIZE = 3
    var YSIZE = 3
    val DX = 2.0 / (XSIZE * 0.707106781)
    val DY = 1.0 / (YSIZE + 1.0)

    var converter = SpectralToPhysicalConverter(startState)

    val TIMESTEPS_PER_WINDOW = 2
    val STREAMFUNCTION_ITERATIONS = 40
    val RELAXATION = 0.4 // n.b. over-relaxation to speed convergence

    // Prognostic variables
    lateinit var ita: Field<DOUBLE>   // Vorticity = Laplacian(Psi)
    lateinit var theta: Field<DOUBLE> // Temperature perturbation from linear

    // Diagnostic variable
    lateinit var psi: Field<DOUBLE>   // Streamfunction

    init{ initialise() }


    fun initialise() {
        psi = Field(converter.getPsi(XSIZE, YSIZE), domain)
        theta = Field(converter.getTheta(XSIZE, YSIZE), domain)
        ita = Field(Laplacian(psi))
    }

    override fun runWindow(): Collection<DOUBLE> {
        if (!::ita.isInitialized) { initialise() }
        val observations = ArrayList<DOUBLE>(TIMESTEPS_PER_WINDOW)
        for (i in 1..TIMESTEPS_PER_WINDOW) {
            //println("" + theta[20, 20] + " " + ita[20, 20] + " " + psi[20, 20])
            println("Starting iteration: " + i)
            step()
            observations.add(observe())
            println("Finished iteration: " + i)
        }
        return observations
    }

    private fun observe(): DOUBLE {
        val temp = theta[2, 2]
        return random.nextGaussian(temp, 1.0)
    }

    fun computeDita(): Field<DOUBLE> {
        return Field((d_dx(ita) * d_dy(psi)
                           - d_dx(psi) * d_dy(ita)
                           + (d2_dx2(ita) + d2_dy2(ita)) * NU
                           + d_dx(theta) * GA)
                           * DT)
    }

    fun computeDtheta(): Field<DOUBLE> {
        return Field((d_dx(theta) * d_dy(psi)
                            - d_dx(psi) * d_dy(theta)
                            + d_dx(psi)
                            + (d2_dx2(theta) + d2_dy2(theta)) * KAPPA)
                            * DT)
    }

    fun solveStreamfunction() {
        var DX2 = DX * DX
        var DY2 = DY * DY
        for (i in 0 until STREAMFUNCTION_ITERATIONS) {
//            println("streamfunction iteration " + i)
            psi = Field(((ShiftN(psi) + ShiftS(psi))/DY2 + (ShiftE(psi) + ShiftW(psi))/DX2 - ita)
                               * (RELAXATION / (2.0/DX2 + 2.0/DY2))
                               + psi * (1.0 - RELAXATION))
        }
        // TODO Pretty sure this adds about 250,000 vertices to our graph...
//        for (i in 0 until STREAMFUNCTION_ITERATIONS) {
//            psi = Field(((ShiftN(psi) + ShiftS(psi))/DY2
//                             + (ShiftE(psi) + ShiftW(psi))/DX2
//                             - ita)
//                             / (2.0/DX2 + 2.0/DY2))
//        }
    }

    override fun step() {
        // Compute the differences
        println("Computing Dita")
        val Dita = computeDita()
        println("Computing Dtheta")
        val Dtheta = computeDtheta()

        // Increment the prognostic variables
        println("Updating prognostic variables")
        ita = Field(ita + Dita)
        theta = Field(theta + Dtheta)

        // Solve the Poisson equation for psi
        println("Solving streamfunction")
        solveStreamfunction()
    }

    override fun getState(): Collection<DOUBLE> {
        println("Getting state...")
        return psi.data.union(theta.data)
    }

    override fun setState(state: Collection<DOUBLE>) {
//        var chunks = state.chunked(state.size/2)
//        var psiData = Array2D(XSIZE, YSIZE, chunks[0] as ArrayList<DOUBLE>)
//        psi = Field(psiData, domain)
//        var thetaData = Array2D(XSIZE, YSIZE, chunks[1] as ArrayList<DOUBLE>)
//        theta = Field(thetaData, domain)
    }

    fun solve() {
        initialise()
        while (t < TLENGTH) {
            t += DT
            step()
        }
    }

    companion object {
        val SIGMA = 10.0
        val BETA = 2.66667
        val RHO = 28.0
        val DT = 0.01

        val KAPPA = 1.0
        val R = 28.0
        val NU = SIGMA * KAPPA

        // Derived values
        val C = 657.524
        val OPA2 = 1.5
        val GA = R * NU * KAPPA * C
        val A = 0.707106781
    }
}

