package rayleighBenard

import io.improbable.swayze.finiteDifference.Array2D
import io.improbable.keanu.kotlin.DoubleOperators
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

open class SpectralToPhysicalConverter<T : DoubleOperators<T>>(var x: T, var y: T, var z: T) {

    constructor(it: Iterator<T>): this(it.next(), it.next(), it.next())

    fun getPsi(xsize : Int, ysize : Int) : Array2D<T> {
        val DX = 2.0 / (xsize * 0.707106781)
        val DY = 1.0 / (ysize + 1.0)
        val psi = Array2D(xsize, ysize, {ii,ij ->
            val i = DX * ii
            val j = DY * (1 + ij)
            (x * sqrt(2.0) * KAPPA * OPA2 / A) * sin(PI * A * i) * sin(PI * j)
        })
        return psi
    }


    fun getVelocity(xsize : Int, ysize : Int) : Array2D<Pair<T, T>> {
        val DX = 2.0 / (xsize * 0.707106781)
        val DY = 1.0 / (ysize + 1.0)
        val v = Array2D(xsize, ysize, {ii,ij ->
            val i = DX * ii
            val j = DY * (1 + ij)
            Pair(
                    (x * -sqrt(2.0)*KAPPA * OPA2 * PI / A)* sin(PI * A * i) * cos(PI * j),
                    (x * sqrt(2.0) * KAPPA * OPA2 * PI) * cos(PI * A * i) * sin(PI * j)
            )
        })
        return v
    }


    fun getTheta(xsize : Int, ysize : Int) : Array2D<T> {
        val DX = 2.0 / (xsize * 0.707106781)
        val DY = 1.0 / (ysize + 1.0)
        val theta = Array2D(xsize, ysize, {ii,ij ->
            val i = DX * ii
            val j = DY * (1 + ij)
            ((y * sqrt(2.0) / (PI * R)) * cos(PI * A * i) * sin(PI * j)
                    - (z / (PI * R)) * sin(2.0 * PI * j))

        })
        return theta
    }


    fun getDeltaT(xPos : Double, yPos : Double) : T {
        return z * sin(2.0 * PI * yPos) / sqrt(2.0) -
               y * cos(2.0 * PI * xPos) * sin(PI * yPos)
    }


    override fun toString(): String {
        return "$x $y $z"
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