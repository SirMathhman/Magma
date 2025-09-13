package magma;

import java.util.List;
import java.util.Objects;

/**
 * InterpreterError is a simple data holder representing interpreter failures.
 */
public final class InterpreterError {
	private final String message;
	private final String sourceCode;
	private final List<InterpreterError> children;

	public InterpreterError() {
		this("interpreter error", "", List.of());
	}

	public InterpreterError(String message) {
		this(message, "", List.of());
	}

	public InterpreterError(String message, String sourceCode, List<InterpreterError> children) {
		this.message = Objects.requireNonNullElse(message, "");
		this.sourceCode = Objects.requireNonNullElse(sourceCode, "");
		this.children = List.copyOf(Objects.requireNonNullElse(children, List.of()));
	}

	public String getMessage() {
		return message;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public List<InterpreterError> getChildren() {
		return children;
	}
}
