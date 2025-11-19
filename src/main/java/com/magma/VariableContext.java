package com.magma;

import java.util.HashMap;
import java.util.Map;

/**
 * Context for storing and retrieving variables.
 */
public final class VariableContext {
    /**
     * Map of variable names to their values.
     */
    private final Map<String, String> variables;

    /**
     * Creates a new variable context.
     */
    public VariableContext() {
        this.variables = new HashMap<>();
    }

    /**
     * Sets a variable value.
     *
     * @param name  The variable name
     * @param value The variable value
     */
    public void setVariable(final String name, final String value) {
        this.variables.put(name, value);
    }

    /**
     * Gets a variable value.
     *
     * @param name The variable name
     * @return The variable value, or empty Optional if not found
     */
    public java.util.Optional<String> getVariable(final String name) {
        return java.util.Optional.ofNullable(this.variables.get(name));
    }

    /**
     * Gets all variable names.
     *
     * @return Set of variable names
     */
    public java.util.Set<String> getVariableNames() {
        return this.variables.keySet();
    }
}

