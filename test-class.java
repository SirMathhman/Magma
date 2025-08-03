import magma.Main;

public class TestClass {
    public static void main(String[] args) {
        // Test class functionality
        String result = Main.run("class fn Wrapper() => {let x = 100;} Wrapper().x");
        System.out.println("Result: " + result);
        System.out.println("Expected: 100");
        System.out.println("Test " + (result.equals("100") ? "PASSED" : "FAILED"));
    }
}