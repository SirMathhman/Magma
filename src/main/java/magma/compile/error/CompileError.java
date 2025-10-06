package magma.compile.error;

import magma.compile.context.Context;
import magma.list.ArrayList;
import magma.list.Collections;
import magma.list.Joiner;
import magma.list.List;
import magma.list.Max;
import magma.list.Stream;
import magma.option.Option;

import java.util.Comparator;

public record CompileError(String reason, Context context, List<CompileError> causes) implements Error {
	public CompileError(String reason, Context sourceCode) {
		this(reason, sourceCode, Collections.emptyList());
	}

	@Override
	public String display() {
		return format(0, new ArrayList<Integer>());
	}

	private String format(int depth, List<Integer> indices) {
		final List<CompileError> copy = causes.copy();
		copy.sort(Comparator.comparingInt(CompileError::depth));
		final String formattedChildren = joinErrors(depth, indices, copy);
		final String s;
		if (depth == 0)
			s = "";
		else
			s = System.lineSeparator() + "\t".repeat(depth);

		final String joinedIndices = getCollect(indices);
		return s + joinedIndices + ") " + reason + ": " + context.display(depth) + formattedChildren;
	}

	private String getCollect(List<Integer> indices) {
		final Stream<Integer> stream = indices.stream();
		final Stream<String> stringStream = stream.map(String::valueOf);
		return stringStream.collect(new Joiner("."));
	}

	private String joinErrors(int depth, List<Integer> indices, List<CompileError> copy) {
		final Stream<Integer> range = Stream.range(0, copy.size());
		final Stream<String> stringStream = range.map(index -> formatChild(depth, copy, indices, index));
		return stringStream.collect(new Joiner(""));
	}

	private String formatChild(int depth, List<CompileError> copy, List<Integer> indices, int last) {
		CompileError error = copy.getOrNull(last);
		indices.push(last);
		final String format = error.format(depth + 1, indices);
		indices.pop();
		return format;
	}

	private int depth() {
		final Stream<Integer> intStream = causes.stream().map(CompileError::depth);
		final Option<Integer> max = intStream.collect(new Max());
		return 1 + max.orElse(0);
	}
}
