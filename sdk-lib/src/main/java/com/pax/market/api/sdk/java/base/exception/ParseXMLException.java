package com.pax.market.api.sdk.java.base.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Created by wing on 2018/7/4.
 */

public class ParseXMLException extends Exception {
    /** A wrapped <code>Throwable</code> */
    private Throwable nestedException;

    public ParseXMLException() {
        super("Error occurred in parse download parameter xml.");
    }

    public ParseXMLException(String message) {
        super(message);
    }

    public ParseXMLException(Throwable nestedException) {
        super(nestedException.getMessage());
        this.nestedException = nestedException;
    }

    public ParseXMLException(String message, Throwable nestedException) {
        super(message);
        this.nestedException = nestedException;
    }

    public Throwable getNestedException() {
        return nestedException;
    }

    public String getMessage() {
        if (nestedException != null) {
            return super.getMessage() + " Nested exception: "
                    + nestedException.getMessage();
        } else {
            return super.getMessage();
        }
    }

    public void printStackTrace() {
        super.printStackTrace();

        if (nestedException != null) {
            System.err.print("Nested exception: ");
            nestedException.printStackTrace();
        }
    }

    public void printStackTrace(PrintStream out) {
        super.printStackTrace(out);

        if (nestedException != null) {
            out.println("Nested exception: ");
            nestedException.printStackTrace(out);
        }
    }

    public void printStackTrace(PrintWriter writer) {
        super.printStackTrace(writer);

        if (nestedException != null) {
            writer.println("Nested exception: ");
            nestedException.printStackTrace(writer);
        }
    }
}