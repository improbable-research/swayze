package rayleighBenard

import gnuPlotLib.GnuplotController
import io.improbable.keanu.kotlin.ArithmeticDouble
import io.improbable.keanu.vertices.dbl.DoubleVertex
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import java.awt.Toolkit


class PlotFluid : GnuplotController {
    val XSIZE = 30
    val YSIZE = 24

    val colourbarMax = 0.75
    val colourbarMin = -0.75

    constructor() {
        val screenSize = Toolkit.getDefaultToolkit().screenSize
//        println("Screensize is ${screenSize.width},${screenSize.height}")
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

    fun velocity(fluid: SpectralToPhysicalConverter<ArithmeticDouble>) {
        writeln("set xrange [0:${XSIZE + 1}]")
//        write("plot '-' using 1:2:(0.5):5 with circles linecolor palette z fillstyle solid\n")
        writeln("plot '-' with vectors linecolor palette z lw 2 head filled")
        writeXYDataFrame(toGNUplotMatrix(fluid, XSIZE, YSIZE))
    }


    fun velocity(leftModel: SpectralToPhysicalConverter<ArithmeticDouble>, rightModel: SpectralToPhysicalConverter<ArithmeticDouble>) {
        writeln("set xrange [0:${2*XSIZE + 10}]")
        writeln("plot '-' with vectors linecolor palette z lw 2")
        writeXYDataFrame(toGNUplotMatrix(leftModel, rightModel, XSIZE, YSIZE))
    }

    fun temperature(fluid: SpectralToPhysicalConverter<ArithmeticDouble>) {
//        window.write("plot '-' using 1:2:5 with points pt 18 ps 4.7 linecolor palette z")
        writeln("splot '-' using 1:2:5 with lines")
        writeXYDataFrame(toGNUplotMatrix(fluid, XSIZE, YSIZE))
    }

    fun temperature(fluid1: SpectralToPhysicalConverter<ArithmeticDouble>, fluid2 : SpectralToPhysicalConverter<ArithmeticDouble>) {
        writeln("splot '-' using 1:2:5 with lines")
        writeXYDataFrame(toGNUplotMatrix(fluid1, fluid2, XSIZE, YSIZE))
    }

    fun getMeanState(model: SpectralConvection<DoubleVertex>): SpectralToPhysicalConverter<ArithmeticDouble> {
        val it = model.getState().iterator()
        return SpectralToPhysicalConverter(
                ArithmeticDouble(it.next().value),
                ArithmeticDouble(it.next().value),
                ArithmeticDouble(it.next().value))
    }

    fun toGNUplotMatrix(convectionProblem: SpectralToPhysicalConverter<ArithmeticDouble>, xsize: Int, ysize: Int): Array2DRowRealMatrix {
        val matrix = Array2DRowRealMatrix(xsize * ysize, 5)
        var velocityField = convectionProblem.getVelocity(xsize, ysize)
        var temperaturePerturbationField = convectionProblem.getTheta(xsize, ysize)
        var m_j = 0
        for (i in 0 until xsize) {
            for (j in 0 until ysize) {
                matrix.setEntry(m_j, 0, 1.0 * i)
                matrix.setEntry(m_j, 1, 1.0 * j)
                matrix.setEntry(m_j, 2, velocityField[i, j].first.value / 20.0)
                matrix.setEntry(m_j, 3, velocityField[i, j].second.value / 20.0)
                matrix.setEntry(m_j, 4, temperaturePerturbationField[i, j].value)
                m_j += 1
            }
        }
        return matrix
    }

    fun toGNUplotMatrix(model1: SpectralToPhysicalConverter<ArithmeticDouble>, model2: SpectralToPhysicalConverter<ArithmeticDouble>, xsize: Int, ysize: Int) : Array2DRowRealMatrix {
        val matrix = Array2DRowRealMatrix((xsize*2+10)*ysize, 5)
        var velocityField = model1.getVelocity(xsize, ysize)
        var temperaturePerturbationField = model1.getTheta(xsize, ysize)
        var m_j = 0
        for (i in 0 until xsize) {
            for (j in 0 until ysize) {
                matrix.setEntry(m_j, 0, 1.0*i)
                matrix.setEntry(m_j, 1, 1.0*j)
                matrix.setEntry(m_j, 2, velocityField[i,j].first.value/20.0)
                matrix.setEntry(m_j, 3, velocityField[i,j].second.value/20.0)
                matrix.setEntry(m_j, 4, temperaturePerturbationField[i, j].value)
                m_j += 1
            }
        }
        for (i in xsize until xsize+10) {
            for (j in 0 until ysize) {
                matrix.setEntry(m_j, 0, 1.0*i)
                matrix.setEntry(m_j, 1, 1.0*j)
                matrix.setEntry(m_j, 2, 0.0)
                matrix.setEntry(m_j, 3, 0.0)
                matrix.setEntry(m_j, 4, -1.0)
                m_j += 1
            }
        }
        velocityField = model2.getVelocity(xsize, ysize)
        temperaturePerturbationField = model2.getTheta(xsize, ysize)
        for (i in 0 until xsize) {
            for (j in 0 until ysize) {
                matrix.setEntry(m_j, 0, 1.0*i + xsize + 10)
                matrix.setEntry(m_j, 1, 1.0*j)
                matrix.setEntry(m_j, 2, velocityField[i,j].first.value/20.0)
                matrix.setEntry(m_j, 3, velocityField[i,j].second.value/20.0)
                matrix.setEntry(m_j, 4, temperaturePerturbationField[i, j].value)
                m_j += 1
            }
        }
        return matrix
    }
}