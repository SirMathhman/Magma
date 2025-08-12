package magma;

import org.junit.jupiter.api.Test;

class DebugTest {
    @Test
    void debug() {
        try {
            String result = Compiler.compile("class fn Calculator() => { fn method() => { }");
            System.out.println("RESULT: " + result);
        } catch (CompileException e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }
    }
}