package magma.compile.error;

import magma.compile.context.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
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
		final String formattedChildren = joinErrors(depth, copy);
		final String s = depth == 0 ? "" : System.lineSeparator() + "\t".repeat(depth);
		return s + index + ") " + reason + ": " + context.display(depth) + formattedChildren;
	}

	private String joinErrors(int depth, List<CompileError> copy) {
		return IntStream.range(0, copy.size())
										.mapToObj(index -> formatChild(depth, copy, index))
										.collect(Collectors.joining());
	}

	private String formatChild(int depth, List<CompileError> copy, int i) {
		CompileError error = copy.get(i);
		return error.format(depth + 1, i);
	}

	private int depth() {
		return 1 + causes.stream().mapToInt(CompileError::depth).max().orElse(0);
	}
}
