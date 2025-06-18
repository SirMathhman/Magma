package magma;

import magma.app.ApplicationBuilder;

public class Main {
    public static void main(String[] args) {
        final var application = ApplicationBuilder.create();
        application.run()
                .ifPresent(error -> System.err.println(error.display()));
    }
}
