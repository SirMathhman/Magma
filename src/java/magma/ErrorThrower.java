package magma;

/**
 * A utility class that provides a function which always throws an error.
 */
public class ErrorThrower {
    
    /**
     * A function that returns a string or throws a RuntimeException.
     * 
     * @param message The message to include in the exception or return
     * @return An empty string if the input is empty
     * @throws RuntimeException Thrown with the provided message if the input is not empty
     */
    public static String throwError(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }
        throw new RuntimeException(message);
    }
}