package gnuPlotLib

import io.improbable.swayze.finiteDifference.*
import io.improbable.keanu.kotlin.ArithmeticDouble
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import java.awt.Toolkit

/**
 * Extending gnuPlotLib to add some specific functionality for plotting 2D fields. As needed for 2D Navier Stokes problems
 */
class PlotField : GnuplotController {

    var XSIZE = 30
    var YSIZE = 24
    var scalingNumber = 2.0 // A magic number to scale the values to make the plot nice
    var colourbarMax = 0.75
    var colourbarMin = -0.75

    constructor()

    fun initialise() {
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        writeln("set terminal x11 size ${(screenSize.width*0.75).toInt()},${(screenSize.height*0.75).toInt()}")
        writeln("set yrange [0:$YSIZE]")
        writeln("set zrange [-0.75:0.75]")
        writeln("set cbrange [${colourbarMin}:${colourbarMax}]")
        writeln("unset xtics")
        writeln("unset ytics")
        writeln("unset ztics")
        writeln("set pm3d")
        writeln("set view 0,0")
    }

    fun R2VectorIn2D (u: Field<ArithmeticDouble>, v: Field<ArithmeticDouble>) {
        writeln("set xrange [0:${XSIZE + 1}]")
        writeln("plot '-' with vectors lw 2 head filled") //linecolor palette z")// lw 2 head filled") // Todo CHECK: Maybe expecting 5 column matrix?
        writeXYDataFrame(vectorToGNUplotMatrix(u, v, XSIZE, YSIZE))
    }

    fun scalarIn2D (f: Field<ArithmeticDouble>) {
        writeln("set xrange [0:${XSIZE + 1}]")
        writeln("splot '-' using 1:2:3 with lines")
        writeXYDataFrame(scalarToGNUplotMatrix(f, XSIZE, YSIZE))
    }

    fun R2VectorMagnitudeAndScalarIn2D (u: Field<ArithmeticDouble>, v: Field<ArithmeticDouble>, f: Field<ArithmeticDouble>) {
        var vectorMagnitude = dot(u, v)
        writeln("splot '-' using 1:2:3 with lines")
        writeXYDataFrame(scalarToGNUplotMatrix(vectorMagnitude, f, XSIZE, YSIZE))
    }

    fun vectorToGNUplotMatrix(u: Field<ArithmeticDouble>, v: Field<ArithmeticDouble>, xsize: Int, ysize: Int): Array2DRowRealMatrix {
        val matrix = Array2DRowRealMatrix(xsize * ysize, 4)
        var m_j = 0
        for (i in 0 until xsize) {
            for (j in 0 until ysize) {
                matrix.setEntry(m_j, 0, 1.0 * i)
                matrix.setEntry(m_j, 1, 1.0 * j)
                matrix.setEntry(m_j, 2, u[i, j].value / scalingNumber)
                matrix.setEntry(m_j, 3, v[i, j].value / scalingNumber)
                m_j += 1
            }
        }
        return matrix
    }

    fun vectorToGNUplotMatrix(u: Field<ArithmeticDouble>, v: Field<ArithmeticDouble>): Array2DRowRealMatrix {
        // TODO check that u & v are the same size
        return vectorToGNUplotMatrix(u, v, u.iSize(), u.jSize())
    }

    fun scalarToGNUplotMatrix(f: Field<ArithmeticDouble>, xsize: Int, ysize: Int): Array2DRowRealMatrix {
        val matrix = Array2DRowRealMatrix(xsize * ysize, 3)
        var m_j = 0
        for (i in 0 until xsize) {
            for (j in 0 until ysize) {
                matrix.setEntry(m_j, 0, 1.0 * i)
                matrix.setEntry(m_j, 1, 1.0 * j)
                matrix.setEntry(m_j, 2, f[i, j].value)
                m_j += 1
            }
        }
        return matrix
    }

    fun scalarToGNUplotMatrix(f: Field<ArithmeticDouble>): Array2DRowRealMatrix {
        var xsize = f.iSize()
        var ysize = f.jSize()
        return scalarToGNUplotMatrix(f, xsize, ysize)
    }

    fun scalarToGNUplotMatrix(f1: Field<ArithmeticDouble>, f2: Field<ArithmeticDouble>, xsize: Int, ysize: Int): Array2DRowRealMatrix {
        val matrix = Array2DRowRealMatrix(xsize * ysize, 3)
        var m_j = 0
        for (i in 0 until xsize) {
            for (j in 0 until ysize) {
                matrix.setEntry(m_j, 0, 1.0 * i)
                matrix.setEntry(m_j, 1, 1.0 * j)
                matrix.setEntry(m_j, 2, f1[i, j].value)
                m_j += 1
            }
        }
        for (i in xsize until xsize+10) {
            for (j in 0 until ysize) {
                matrix.setEntry(m_j, 0, 1.0*i)
                matrix.setEntry(m_j, 1, 1.0*j)
                matrix.setEntry(m_j, 2, -1.0)
                m_j += 1
            }
        }
        for (i in 0 until xsize) {
            for (j in 0 until ysize) {
                matrix.setEntry(m_j, 0, 1.0*i + xsize + 10)
                matrix.setEntry(m_j, 1, 1.0*j)
                matrix.setEntry(m_j, 2, f2[i, j].value)
                m_j += 1
            }
        }
        return matrix
    }
}