package magma.compile.error;

import magma.compile.context.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public record CompileError(String reason, Context context, List<CompileError> causes) implements Error {
	public CompileError(String reason, Context sourceCode) {
		this(reason, sourceCode, Collections.emptyList());
	}

	@Override
	public String display() {
		return format(0, 0);
	}

	private String format(int depth, int index) {
		final ArrayList<CompileError> copy = new ArrayList<>(causes);
		copy.sort(Comparator.comparingInt(CompileError::depth));
		final StringBuilder joiner = joinErrors(depth, copy);
		final String formattedChildren = joiner.toString();
		final String s = depth == 0 ? "" : System.lineSeparator() + "\t".repeat(depth);
		return s + index + ") " + reason + ": " + context.display(depth) + formattedChildren;
	}

	private StringBuilder joinErrors(int depth, List<CompileError> copy) {
		StringBuilder joiner = new StringBuilder();
		IntStream.range(0, copy.size()).forEach(i -> {
			CompileError error = copy.get(i);
			String format = error.format(depth + 1, i);
			joiner.append(format);
		});
		return joiner;
	}

	private int depth() {
		return 1 + causes.stream().mapToInt(CompileError::depth).max().orElse(0);
	}
}
