public class TestOutput {
    public static void main(String[] args) {
        // Test explicit char type declarations
        String javaCode = """
                let a : Char = 'a';
                let b : Char = 'b';
                let c : Char = '\\n'; // Newline character
                let d : Char = '\\t'; // Tab character""";
        
        String cCode = Main.compile(javaCode);
        System.out.println("=== Compiled C Code ===");
        System.out.println(cCode);
        System.out.println("=== End of Compiled C Code ===");
    }
}