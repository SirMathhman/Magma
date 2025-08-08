package magma;

/**
 * A simple demonstration of the StringProcessor class.
 */
public class StringProcessorDemo {
    
    /**
     * Main method to demonstrate the StringProcessor behavior.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        StringProcessor processor = new StringProcessor();
        
        // Test with empty input
        String emptyInput = "";
        try {
            String result = processor.process(emptyInput);
            System.out.println("Input: \"" + emptyInput + "\"");
            System.out.println("Output: \"" + result + "\"");
        } catch (Exception e) {
            System.out.println("Exception for empty input: " + e.getMessage());
        }
        
        // Test with non-empty input
        String nonEmptyInput = "hello";
        try {
            String result = processor.process(nonEmptyInput);
            System.out.println("Input: \"" + nonEmptyInput + "\"");
            System.out.println("Output: \"" + result + "\"");
        } catch (Exception e) {
            System.out.println("Exception for non-empty input: " + e.getMessage());
        }
    }
}