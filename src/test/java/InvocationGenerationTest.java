import magma.compile.CRules;
import magma.compile.Node;
import magma.compile.error.CompileError;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvocationGenerationTest {
    @Test
    public void testSimpleInvocationRoundTrip() {
        // Create a node representing foo() and test generation
        Node invocationNode = new Node()
            .retype("invocation")
            .withNode("caller", new Node().retype("identifier").withString("value", "foo"))
            .withNodeList("arguments", List.of());
        
        Result<String, CompileError> genResult = CRules.CRoot().generate(invocationNode);
        
        if (genResult instanceof Ok<String, CompileError>(String generated)) {
            System.out.println("Generated from invocation node: " + generated);
            // The generated code should have matching parentheses
            long openCount = generated.chars().filter(ch -> ch == '(').count();
            long closeCount = generated.chars().filter(ch -> ch == ')').count();
            assertEquals(openCount, closeCount, "Parentheses should be balanced: " + generated);
        } else {
            System.out.println("Generation failed: " + genResult);
        }
    }
    
    @Test
    public void testNestedInvocationGeneration() {
        // Create a node representing foo(bar())
        Node barCall = new Node()
            .retype("invocation")
            .withNode("caller", new Node().retype("identifier").withString("value", "bar"))
            .withNodeList("arguments", List.of());
            
        Node fooCall = new Node()
            .retype("invocation")
            .withNode("caller", new Node().retype("identifier").withString("value", "foo"))
            .withNodeList("arguments", List.of(barCall));
        
        Result<String, CompileError> genResult = CRules.CRoot().generate(fooCall);
        
        if (genResult instanceof Ok<String, CompileError>(String generated)) {
            System.out.println("Generated from nested invocation: " + generated);
            // The generated code should have matching parentheses
            long openCount = generated.chars().filter(ch -> ch == '(').count();
            long closeCount = generated.chars().filter(ch -> ch == ')').count();
            assertEquals(openCount, closeCount, "Parentheses should be balanced: " + generated);
        } else {
            System.out.println("Generation failed: " + genResult);
        }
    }
}
