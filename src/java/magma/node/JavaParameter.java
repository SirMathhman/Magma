package magma.node;

/**
 * Base interface for all code generation nodes in the Magma system.
 * 
 * This interface defines the contract for objects that can generate code as strings.
 * It serves as the foundation of the node hierarchy used in the code generation process.
 * All concrete node types implement this interface, either directly or through a subinterface.
 * 
 * The node hierarchy includes:
 * <ul>
 *   <li>JavaParameter (this interface) - Base for all nodes</li>
 *   <li>JavaMethodHeader - Represents method headers</li>
 *   <li>CDefinition - Represents C-style definitions</li>
 *   <li>JavaConstructor - Represents Java constructors</li>
 *   <li>Placeholder - Represents comments or placeholders in generated code</li>
 * </ul>
 * 
 * Example usage:
 * <pre>
 * JavaParameter param = new CDefinition("int", "count");
 * String code = param.generate();  // "int count"
 * </pre>
 */
public interface JavaParameter {
	/**
	 * Generates the string representation of this node.
	 * 
	 * This method is implemented by all concrete node classes to produce
	 * their specific code representation as a string.
	 *
	 * @return The generated code as a string
	 */
	String generate();
}
