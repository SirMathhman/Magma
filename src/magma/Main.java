package magma;

public class Main {
    public static void main(String[] args) {
        try {
            new Application(new DirectorySourceSet(), new PathTargetSet()).run();
        } catch (CompileException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
