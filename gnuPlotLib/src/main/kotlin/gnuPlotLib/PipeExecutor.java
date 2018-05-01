package gnuPlotLib;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.*;

/***
 * Executes a supplied command, and sets up pipes to write to the executable's standard in
 * and read from its standard out and standard error
 */
public class PipeExecutor {

    PipedOutputStream stdin = new PipedOutputStream(); // pipe feeding the executable's standard in
    PipedInputStream stderr = new PipedInputStream();  // pipe fed from the standard error of the executable
    PipedInputStream stdout = new PipedInputStream();  // pipe fed from the standard out of the executable
    PumpStreamHandler outPump = null;
    PumpStreamHandler inPump = null;
    Thread execThread;

    PipeExecutor(String command) throws IOException {
        PipedOutputStream execOut = new PipedOutputStream();
        PipedOutputStream execErr = new PipedOutputStream();
        PipedInputStream execIn = new PipedInputStream();

        Executor exec = new DefaultExecutor();

        execIn.connect(stdin);
        execOut.connect(stdout);
        execErr.connect(stderr);

        exec.setStreamHandler(new PumpStreamHandler(execOut, execErr, execIn));
        CommandLine cl = parseCommand(command);
        execThread = new Thread(() -> {
            try {
                exec.execute(cl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        execThread.start();
    }

    public void stop() throws IOException {
        stdin.close();
        if(inPump != null) outPump.stop();
        if(outPump != null) outPump.stop();
    }

    public void flush() throws IOException {
        stdin.flush();
    }

    public void write(int i) throws IOException {
        stdin.write(i);
    }

    public void write(byte[] buff) throws IOException {
        stdin.write(buff);
    }

    public void write(byte[] buff, int offset, int length) throws IOException {
        stdin.write(buff, offset, length);
    }

    public int read() throws IOException {
        return stdout.read();
    }

    public int read(byte[] buff) throws IOException {
        return stdout.read(buff);
    }

    public int read(byte[] buff, int offset, int length) throws IOException {
        return stdout.read(buff,offset,length);
    }

    public int readErr() throws IOException {
        return stderr.read();
    }

    public int readErr(byte[] buff) throws IOException {
        return stderr.read(buff);
    }

    public int readErr(byte[] buff, int offset, int length) throws IOException {
        return stderr.read(buff,offset,length);
    }

    /**
     * Connects the supplied output stream to the stdout of the executable
     * @param out the stream to connect to the executable's stdout, or null to remove an existing stream
     */
    public void pipeTo(OutputStream out) throws IOException {
        if(outPump != null) {
            outPump.stop();

        }
        outPump = createPump(stdout, out);
    }

    /**
     * Connects the supplied input stream to the stdin of the executable
     * @param in the stream to connect to the executable's stdin, or null to remove an existing stream
     */
    public void pipeFrom(InputStream in) throws IOException {
        if(inPump != null) inPump.stop();
        inPump = createPump(in, stdin);
    }

    /**
     *
     * @return true if the main execution thread is alive (i.e. is running and hasn't terminated)
     */
    public boolean isAlive() {
        return execThread.isAlive();
    }


    /**
     * waits for the main execution thread to terminate
     */
    public void waitForTermination() throws InterruptedException {
        while(isAlive()) Thread.sleep(1);
    }

    PumpStreamHandler createPump(InputStream in, OutputStream out) {
        if(in == null || out == null) return null;
        PumpStreamHandler psh = new PumpStreamHandler(out);
        psh.setProcessOutputStream(in);
        psh.start();
        return psh;
    }

    CommandLine parseCommand(String command) {
        String [] args = command.split(" ",2);
        CommandLine cl = null;
        if(args.length > 0) {
            cl = new CommandLine(args[0]);
            if(args.length > 1) {
                cl.addArguments(args[1],true);
            }
        }
        return cl;
    }
}
