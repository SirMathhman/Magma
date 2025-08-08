package magma;

/**
 * A simple class that processes strings but is stubbed to always throw an error.
 */
public class StringProcessor {
    
    /**
     * Processes the input string.
     *
     * @param input The string to process
     * @return An empty string if the input is empty, otherwise throws an exception
     * @throws UnsupportedOperationException Thrown to indicate the method is not fully implemented yet
     */
    public String process(String input) {
        if (input.isEmpty()) {
            return "";
        }
        throw new UnsupportedOperationException("This method is not implemented yet");
    }
}