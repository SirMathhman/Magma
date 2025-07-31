package magma.error;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an error that occurred during compilation.
 * A CompileError has a message, an optional context, and an optional list of child errors.
 */
public class CompileError {
	private final String message;
	private final Context context;
	private final List<CompileError> children;

	/**
	 * Creates a new CompileError with the specified message and no context or children.
	 *
	 * @param message the error message
	 */
	public CompileError(final String message) {
		this(message, null, Collections.emptyList());
	}

	/**
	 * Creates a new CompileError with the specified message and context, but no children.
	 *
	 * @param message the error message
	 * @param context the context in which the error occurred
	 */
	public CompileError(final String message, final Context context) {
		this(message, context, Collections.emptyList());
	}

	/**
	 * Creates a new CompileError with the specified message, context, and children.
	 *
	 * @param message  the error message
	 * @param context  the context in which the error occurred
	 * @param children the child errors
	 */
	public CompileError(final String message, final Context context, final List<CompileError> children) {
		this.message = message; this.context = context; this.children = new ArrayList<>(children);
	}

	/**
	 * Returns the error message.
	 *
	 * @return the error message
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Returns the context in which the error occurred, if any.
	 *
	 * @return the context, or null if no context is available
	 */
	public Context getContext() {
		return this.context;
	}

	/**
	 * Returns the child errors, if any.
	 *
	 * @return an unmodifiable list of child errors
	 */
	public List<CompileError> getChildren() {
		return Collections.unmodifiableList(this.children);
	}

	/**
	 * Adds a child error to this error.
	 *
	 * @param child the child error to add
	 * @return this error (for method chaining)
	 */
	public CompileError addChild(final CompileError child) {
		this.children.add(child); return this;
	}

	/**
	 * Displays this error and its children in a human-readable format.
	 *
	 * @return a string representation of this error and its children
	 */
	public String display() {
		return this.display(0);
	}

	/**
	 * Displays this error and its children in a human-readable format with the specified indentation level.
	 *
	 * @param indentLevel the indentation level
	 * @return a string representation of this error and its children
	 */
	private String display(final int indentLevel) {
		final StringBuilder sb = new StringBuilder(); final String indent = "  ".repeat(indentLevel);

		// Add the error message
		sb.append(indent).append(this.message);

		// Add the context if available
		if (this.context != null) {
			sb.append("\n").append(indent).append("  at ").append(this.context.display());
		}

		// Add the children if any
		for (final CompileError child : this.children) {
			sb.append("\n").append(child.display(indentLevel + 1));
		}

		return sb.toString();
	}
}