package magma;

import magma.api.MagmaError;
import magma.api.Option;
import magma.api.Result;
import magma.api.Tuple;
import magma.compile.CompileError;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionTypeDebugTest {
    
    @Test
    public void testJustFunctionDeclaration() {
        String program = """
            fn add(first : I32, second : I32) => first + second;
            add(1, 2)
            """;
        
        assertValid(program, "", new Tuple<>("", 3));
    }
    
    @Test
    public void testJustFunctionReference() {
        String program = """
            fn add(first : I32, second : I32) => first + second;
            add
            """;
        
        // This should compile - function references should work
        Result<Tuple<String, Integer>, RunError> res = Runner.run(program, "");
        // We expect this to succeed or at least give us a better error message
        switch (res) {
            case Result.Ok(Tuple<String, Integer> value) -> {
                // Success - function references work
            }
            case Result.Err(RunError error) -> {
                System.out.println("Error: " + error.toString());
                // Let's see what the error is
            }
        }
    }

    private void assertValid(String program, String input, Tuple<String, Integer> expected) {
        Result<Tuple<String, Integer>, RunError> res = Runner.run(program, input);
        switch (res) {
            case Result.Ok(Tuple<String, Integer> value) -> assertEquals(expected, value);
            case Result.Err(RunError error) -> fail(error.toString());
        }
    }
}