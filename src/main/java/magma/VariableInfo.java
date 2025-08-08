package magma;

/**
 * A simple class to hold variable information.
 * The mutable flag indicates whether the variable can be reassigned.
 */
record VariableInfo(String name, String type, boolean mutable) {
    /**
     * Constructor with default value for mutable (false).
     *
     * @param name The variable name
     * @param type The variable type
     */
    public VariableInfo(String name, String type) {
        this(name, type, false);
    }
}
