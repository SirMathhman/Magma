package magma;

import java.util.List;
import java.util.Objects;

/**
 * Simple error type returned by the interpreter.
 *
 * errorReason: a short description or offending fragment
 * sourceCode: the full source text related to the error (where available)
 * children: nested errors providing additional context
 */
public record InterpretError(String errorReason, String sourceCode, List<InterpretError> children) {
	/**
	 * Canonical constructor: ensure children are present as an empty list when not
	 * provided.
	 */
	public InterpretError {
		children = Objects.requireNonNullElse(children, List.of());
	}

	/**
	 * Convenience constructor that creates an InterpretError with no children.
	 */
	public InterpretError(String errorReason, String sourceCode) {
		this(errorReason, sourceCode, List.of());
	}

	/**
	 * Render this error and any children recursively with indentation.
	 */
	public String display() {
		StringBuilder sb = new StringBuilder();
		displayInto(sb, 0);
		return sb.toString();
	}

	private void displayInto(StringBuilder sb, int indent) {
		for (int i = 0; i < indent; i++)
			sb.append("  ");
		sb.append(errorReason).append(": ").append(sourceCode).append(System.lineSeparator());
		if (!children.isEmpty()) {
			for (InterpretError child : children) {
				child.displayInto(sb, indent + 1);
			}
		}
	}
}
