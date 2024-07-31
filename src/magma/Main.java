package magma;

public class Main {
    public static void main(String[] args) {
        try {
            var source = Application.ROOT_DIRECTORY.resolve("src")
                    .resolve("magma")
                    .resolve("Main.java");

            var application = new Application(source);
            application.run();
        } catch (ApplicationException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
