public class Runner {
    public static void main(String[] args) {
        Compiler compiler = new Compiler();
        try {
            compiler.compile();
        } catch (CompileException e) {
            System.out.println("CompileException caught: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
