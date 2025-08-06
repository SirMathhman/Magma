package com.magma.compiler;

/**
 * Immutable class to represent the error state of the compiler.
 */
public final class ErrorState {
    private final boolean hadError;
    
    /**
     * Creates a new error state.
     * 
     * @param hadError Whether an error has occurred
     */
    private ErrorState(boolean hadError) {
        this.hadError = hadError;
    }
    
    /**
     * Creates a new error state with no errors.
     * 
     * @return A new error state with no errors
     */
    public static ErrorState noError() {
        return new ErrorState(false);
    }
    
    /**
     * Creates a new error state with an error.
     * 
     * @return A new error state with an error
     */
    public static ErrorState withError() {
        return new ErrorState(true);
    }
    
    /**
     * Checks if an error has occurred.
     * 
     * @return true if an error has occurred, false otherwise
     */
    public boolean hadError() {
        return hadError;
    }
    
    /**
     * Reports an error to the user and returns a new error state.
     * 
     * @param line The line where the error occurred
     * @param where Where the error occurred
     * @param message The error message
     * @return A new error state with an error
     */
    public ErrorState report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        return withError();
    }
    
    /**
     * Resets the error state.
     * 
     * @return A new error state with no errors
     */
    public ErrorState reset() {
        return noError();
    }
}