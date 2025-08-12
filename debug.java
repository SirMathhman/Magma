import magma.Compiler;
import magma.CompileException;

public class debug {
    public static void main(String[] args) {
        try {
            String result = Compiler.compile("class fn Calculator() => { fn method() => { }");
            System.out.println("RESULT: " + result);
        } catch (CompileException e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }
    }
}