package jvm.io;

import magma.api.io.IOError;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public record JVMIOError(IOException e) implements IOError {
    @Override
    public String display() {
        final var writer = new StringWriter();
        this.e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
