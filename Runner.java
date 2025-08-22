public class Runner {
    /**
     * Previously this was the `main` method. It's renamed to `run` to accept a single
     * String and return an int status code.
     *
     * @param input arbitrary string input
     * @return 0 on success, non-zero on failure
     */
    public static int run(String input) {
        Compiler compiler = new Compiler();
        try {
            compiler.compile();
            return 0;
        } catch (CompileException e) {
            System.out.println("CompileException caught: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }

    // Small main wrapper so the class remains runnable during testing.
    public static void main(String[] args) {
        int status = run(args.length > 0 ? args[0] : "");
        System.exit(status);
    }
}
