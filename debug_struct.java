import magma.core.Compiler;

public class debug_struct {
    public static void main(String[] args) {
        try {
            Compiler compiler = new Compiler();
            String input = "struct Point { x : I32, y : I32 } let myPoint = Point { 3, 4 };";
            
            System.out.println("[DEBUG_LOG] Input: " + input);
            System.out.println("[DEBUG_LOG] Input starts with 'struct ': " + input.trim().startsWith("struct "));
            System.out.println("[DEBUG_LOG] Input contains '} let ': " + input.contains("} let "));
            System.out.println("[DEBUG_LOG] Input contains ' = ': " + input.contains(" = "));
            System.out.println("[DEBUG_LOG] Input ends with ';': " + input.endsWith(";"));
            
            String result = compiler.compile(input);
            System.out.println("[DEBUG_LOG] Output: " + result);
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}