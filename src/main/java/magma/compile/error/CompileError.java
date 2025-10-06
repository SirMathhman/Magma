package magma.compile.error;

import magma.compile.context.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record CompileError(String reason, Context context, List<CompileError> causes) implements Error {
	public CompileError(String reason, Context sourceCode) {
		this(reason, sourceCode, Collections.emptyList());
	}

	@Override
	public String display() {
		return format(0, new Stack<>());
	}

	private String format(int depth, Stack<Integer> indices) {
		final ArrayList<CompileError> copy = new ArrayList<>(causes);
		copy.sort(Comparator.comparingInt(CompileError::depth));
		final String formattedChildren = joinErrors(depth, indices, copy);
		final String s;
		if (depth == 0) s = "";
		else s = System.lineSeparator() + "\t".repeat(depth);

		final String joinedIndices = getCollect(indices);
		return s + joinedIndices + ") " + reason + ": " + context.display(depth) + formattedChildren;
	}

	private String getCollect(Stack<Integer> indices) {
		final Stream<Integer> stream = indices.stream();
		final Stream<String> stringStream = stream.map(String::valueOf);
		return stringStream.collect(Collectors.joining("."));
	}

	private String joinErrors(int depth, Stack<Integer> indices, List<CompileError> copy) {
		final IntStream range = IntStream.range(0, copy.size());
		final Stream<String> stringStream = range.mapToObj(index -> formatChild(depth, copy, indices, index));
		return stringStream.collect(Collectors.joining());
	}

	private String formatChild(int depth, List<CompileError> copy, Stack<Integer> indices, int last) {
		CompileError error = copy.get(last);
		indices.push(last);
		final String format = error.format(depth + 1, indices);
		indices.pop();
		return format;
	}

	private int depth() {
		final IntStream intStream = causes.stream().mapToInt(CompileError::depth);
		final OptionalInt max = intStream.max();
		return 1 + max.orElse(0);
	}
}
