package net.redstonecraft.redstoneapi.terminal.internal;

import org.jline.reader.LineReader;

import java.io.PrintStream;
import java.util.Locale;

public class CustomPrintStream extends PrintStream {

    private final LineReader lineReader;

    public CustomPrintStream(PrintStream printStream, LineReader lineReader) {
        super(printStream);
        this.lineReader = lineReader;
    }

    @Override
    public void print(boolean b) {
        lineReader.printAbove(String.valueOf(b));
    }

    @Override
    public void print(char c) {
        lineReader.printAbove(String.valueOf(c));
    }

    @Override
    public void print(int i) {
        lineReader.printAbove(String.valueOf(i));
    }

    @Override
    public void print(long l) {
        lineReader.printAbove(String.valueOf(l));
    }

    @Override
    public void print(float f) {
        lineReader.printAbove(String.valueOf(f));
    }

    @Override
    public void print(double d) {
        lineReader.printAbove(String.valueOf(d));
    }

    @Override
    public void print(char[] s) {
        lineReader.printAbove(new String(s));
    }

    @Override
    public void print(String s) {
        lineReader.printAbove(s);
    }

    @Override
    public void print(Object obj) {
        lineReader.printAbove(obj == null ? "null" : obj.toString());
    }

    @Override
    public void println() {
        lineReader.printAbove("\n");
    }

    @Override
    public void println(boolean x) {
        lineReader.printAbove(x + "\n");
    }

    @Override
    public void println(char x) {
        lineReader.printAbove(x + "\n");
    }

    @Override
    public void println(int x) {
        lineReader.printAbove(x + "\n");
    }

    @Override
    public void println(long x) {
        lineReader.printAbove(x + "\n");
    }

    @Override
    public void println(float x) {
        lineReader.printAbove(x + "\n");
    }

    @Override
    public void println(double x) {
        lineReader.printAbove(x + "\n");
    }

    @Override
    public void println(char[] x) {
        lineReader.printAbove(new String(x) + "\n");
    }

    @Override
    public void println(String x) {
        lineReader.printAbove(x + "\n");
    }

    @Override
    public void println(Object x) {
        lineReader.printAbove((x == null ? "null" : x.toString()) + "\n");
    }

    @Override
    public PrintStream printf(String format, Object... args) {
        lineReader.printAbove(String.format(format, args));
        return this;
    }

    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        lineReader.printAbove(String.format(l, format, args));
        return this;
    }

}
