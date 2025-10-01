struct CompileError(String reason, Context context, List<CompileError> causes) implements Error{};
public CompileError_CompileError(String reason, Context context, List<CompileError> causes) implements Error(char* reason, Context sourceCode) {/*
		this(reason, sourceCode, Collections.emptyList());
	*/}
char* display_CompileError(String reason, Context context, List<CompileError> causes) implements Error() {/*
		return format(0, 0);
	*/}
char* format_CompileError(String reason, Context context, List<CompileError> causes) implements Error(int depth, int index) {/*
		final ArrayList<CompileError> copy = new ArrayList<>(causes);
		copy.sort(Comparator.comparingInt(CompileError::depth));

		StringBuilder joiner = new StringBuilder(); for (int i = 0; i < copy.size(); i++) {
			CompileError error = copy.get(i);
			String format = error.format(depth + 1, i);
			joiner.append(format);
		}

		final String formattedChildren = joiner.toString();
		final String s = depth == 0 ? "" : System.lineSeparator() + "\t".repeat(depth);
		return s + index + ") " + reason + ": " + context.display(depth) + formattedChildren;
	*/}
int depth_CompileError(String reason, Context context, List<CompileError> causes) implements Error() {/*
		return 1 + causes.stream().mapToInt(CompileError::depth).max().orElse(0);
	*/}
