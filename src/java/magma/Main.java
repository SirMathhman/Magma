package magma;

import magma.app.Application;
import magma.app.compile.CompilerImpl;

public class Main {
    public static void main(String[] args) {
        new Application(new CompilerImpl()).run()
                .ifPresent(error -> System.err.println(error.display()));
    }
}
