package gnuPlotLib;

import org.apache.commons.math3.linear.MatrixDimensionMismatchException;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.IOException;

public class GnuplotController extends PipeExecutor {

    public GnuplotController() throws IOException, InterruptedException {
        super("gnuplot");
    }

    public void stop() throws IOException {
        super.stop();
    }

    public void write(String s) throws IOException {
        stdin.write(s.getBytes());
    }

    public void write(Character c) throws IOException {
        stdin.write(c);
    }

    public void writeln(String s) throws IOException {
        stdin.write(s.getBytes());
        stdin.write('\n');
    }

    public void writeXYDataFrame(RealMatrix m) throws IOException {
        writeDataFrame(m,true);
    }

    public void writeDataFrame(RealMatrix m) throws IOException {
        writeDataFrame(m,false);
    }

    public void writeDataFrame(RealMatrix m, Boolean separateFrames) throws IOException {
        Double val;
        for(int i=0; i < m.getRowDimension(); ++i) {
            for(int j=0; j < m.getColumnDimension(); ++j) {
                val = m.getEntry(i,j);
                write(val.toString());
                write(' ');
            }
            write('\n');
            if(separateFrames && (i+1)<m.getRowDimension() && m.getEntry(i,0) != m.getEntry(i+1,0)) {
                write('\n');
            }
        }
        write("e\n");
    }

    public void partialWriteDataFrame(RealMatrix m, Integer noEntries) throws IOException {
        Double val;
        for(int i=0; i < noEntries; ++i) {
            for(int j=0; j < m.getColumnDimension(); ++j) {
                val = m.getEntry(i,j);
                write(val.toString());
                write(' ');
            }
            write('\n');
        }
        write("e\n");
    }

    public void  timeseriesPlot(Timeseries series) throws IOException {
        write("set xrange [0:"+series.getCapacity()+"]\n");
        write("plot '-' with lines\n");
        int x = 0;
        for(double xdata : series) {
            write(x + " " + xdata + "\n");
            ++x;
        }
        write("e\n");
    }

    public void scatterPlot(RealMatrix m) throws IOException {
        switch(m.getColumnDimension()) {
            case 2:
                writeln("plot '-'");
                writeDataFrame(m, false);
                break;
            case 3:
                writeln("splot '-' with points");
                writeDataFrame(m, false);
                break;
            default:
                throw(new MatrixDimensionMismatchException(-1,m.getColumnDimension(),-1,2));
        }
    }

    public void linePlot(RealMatrix m, Double zMin, Double zMax) throws IOException {
        writeln("set zrange [" + zMin + ":" + zMax + "]");
        writeln("set cbrange [" + zMin + ":" + zMax + "]");
        linePlot(m);
    }

    public void linePlot(RealMatrix m) throws IOException {
        switch(m.getColumnDimension()) {
            case 2:
                writeln("plot '-' with lines");
                writeDataFrame(m, false);
                break;
            case 3:
                writeln("set pm3d at b");
                writeln("splot '-' with lines");
                writeDataFrame(m, true);
                break;
            default:
                throw(new MatrixDimensionMismatchException(-1,m.getColumnDimension(),-1,2));
        }
    }

    public void vectorPlot(RealMatrix m) throws IOException {
        switch(m.getColumnDimension()) {
            case 4:
                writeln("plot '-' with vectors");
                writeDataFrame(m, false);
                break;
            case 6:
                writeln("splot '-' with vectors");
                writeDataFrame(m, false);
                break;
            default:
                throw(new MatrixDimensionMismatchException(-1,m.getColumnDimension(),-1,4));
        }
    }

    public void contourPlot(RealMatrix m) throws IOException {
        if(m.getColumnDimension() != 3) throw(new MatrixDimensionMismatchException(-1,m.getColumnDimension(),-1,3));
        writeln("set contour");
        writeln("unset surface");
        writeln("set view map");
        writeln("splot '-' with lines");
        writeDataFrame(m,true);
    }


}
