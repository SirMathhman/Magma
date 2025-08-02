package magma.node;

/**
 * Interface for nodes that represent method headers in the code generation system.
 * 
 * This interface extends {@link JavaParameter} and serves as a marker for nodes that
 * specifically represent method or function headers. It doesn't add any new methods
 * beyond those inherited from JavaParameter, but it establishes a more specific type
 * in the node hierarchy.
 * 
 * Implementations of this interface include:
 * <ul>
 *   <li>{@link CDefinition} - Represents C-style function definitions</li>
 *   <li>{@link JavaConstructor} - Represents Java constructor definitions</li>
 * </ul>
 * 
 * This interface is used in the code generation process to identify and process
 * nodes that represent method headers, allowing for specialized handling of these
 * elements during code generation.
 */
public interface JavaMethodHeader extends JavaParameter {}
