package magma.node;

import magma.option.None;
import magma.option.Option;

/**
 * A node that represents a C-style definition with optional type parameters.
 * 
 * This class is used to generate code for variable, parameter, or method definitions
 * in a C-like syntax. It can optionally include a generic type parameter.
 * 
 * Examples of generated code:
 * <ul>
 *   <li>"int count" - A simple variable definition</li>
 *   <li>"&lt;T&gt; List&lt;T&gt; items" - A generic variable definition</li>
 *   <li>"void process" - A method header</li>
 * </ul>
 * 
 * @param maybeTypeParameter An optional generic type parameter
 * @param type The type of the definition (e.g., "int", "String", "void")
 * @param name The name of the variable, parameter, or method
 */
public record CDefinition(Option<String> maybeTypeParameter, String type, String name) implements JavaMethodHeader {
	/**
	 * Creates a new CDefinition without a type parameter.
	 * 
	 * This is a convenience constructor for the common case where no generic type parameter is needed.
	 *
	 * @param type The type of the definition
	 * @param name The name of the variable, parameter, or method
	 */
	public CDefinition(final String type, final String name) {
		this(new None<>(), type, name);
	}

	/**
	 * Generates the string representation of this definition.
	 * 
	 * The format is: "&lt;typeParameter&gt; type name" if a type parameter is present,
	 * or simply "type name" if no type parameter is present.
	 *
	 * @return The generated code as a string
	 */
	@Override
	public String generate() {
		return this.maybeTypeParameter.map(value -> "<" + value + "> ").orElse("") + this.type + " " + this.name;
	}
}
