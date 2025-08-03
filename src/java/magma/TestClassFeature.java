package magma;

public class TestClassFeature {
    public static void main(String[] args) {
        // Test class functionality
        String result = Main.run("class fn Wrapper() => {let x = 100;} Wrapper().x");
        System.out.println("Result: " + result);
        System.out.println("Expected: 100");
        System.out.println("Test " + (result.equals("100") ? "PASSED" : "FAILED"));
        
        // Test method functionality
        String result2 = Main.run("class fn Wrapper() => {fn test() => 200;} Wrapper().test()");
        System.out.println("\nResult: " + result2);
        System.out.println("Expected: 200");
        System.out.println("Test " + (result2.equals("200") ? "PASSED" : "FAILED"));
    }
}