package magma;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            var source = Application.ROOT_DIRECTORY.resolve("src")
                    .resolve("magma")
                    .resolve("Main.java");

            var application = new Application(source);
            application.run();
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
