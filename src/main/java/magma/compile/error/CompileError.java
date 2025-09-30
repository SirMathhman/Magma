package magma.compile.error;

import magma.compile.context.Context;

import java.util.Collections;
import java.util.List;

public record CompileError(String reason, Context context, List<CompileError> causes) implements Error {
	public CompileError(String reason, Context sourceCode) {
		this(reason, sourceCode, Collections.emptyList());
	}

	@Override
	public String display() {
		return format(0, 0);
	}

	private String format(int depth, int index) {
		StringBuilder joiner = new StringBuilder();
		for (int i = 0; i < causes.size(); i++) {
			CompileError error = causes.get(i);
			String format = error.format(depth + 1, i);
			joiner.append(format);
		}

		final String formattedChildren = joiner.toString();
		final String s = depth == 0 ? "" : System.lineSeparator() + "\t".repeat(depth);
		return s + index + ") " + reason + ": " + context.display(depth) + formattedChildren;
	}
}
