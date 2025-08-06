public class test_array {
    public static void main(String[] args) {
        // Test basic array declaration
        String input = "let myArray : [U64; 3] = [1, 2, 3];";
        String expected = "uint64_t myArray[3] = {1, 2, 3};";
        String result = Main.compile(input);
        
        System.out.println("Input: " + input);
        System.out.println("Expected: " + expected);
        System.out.println("Result: " + result);
        System.out.println("Test passed: " + expected.equals(result));
        
        // Test array with whitespace
        input = "let  myArray  :  [ U64 ; 3 ]  =  [ 1 , 2 , 3 ]  ;";
        expected = "uint64_t myArray[3] = {1, 2, 3};";
        result = Main.compile(input);
        
        System.out.println("\nInput: " + input);
        System.out.println("Expected: " + expected);
        System.out.println("Result: " + result);
        System.out.println("Test passed: " + expected.equals(result));
        
        // Test primitive type declaration
        input = "let value : I32 = 42;";
        expected = "int32_t value = 42;";
        result = Main.compile(input);
        
        System.out.println("\nInput: " + input);
        System.out.println("Expected: " + expected);
        System.out.println("Result: " + result);
        System.out.println("Test passed: " + expected.equals(result));
    }
}