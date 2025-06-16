package magma;

import magma.app.Application;
import magma.app.compile.CompilerFactory;

public class Main {
    public static void main(String[] args) {
        final var compiler = CompilerFactory.create();
        new Application(compiler).run()
                .ifPresent(error -> System.err.println(error.display()));
    }
}
