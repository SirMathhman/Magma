// Generated transpiled C++ from 'src\main\java\magma\compile\error\CompileError.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct CompileError{char* reason;Context context;List<CompileError> causes;};
public CompileError_CompileError(char* reason, Context sourceCode) {
	/*this(reason, sourceCode, Collections.emptyList());*/
}
char* display_CompileError() {
	/*return format(0, 0);*/
}
char* format_CompileError(int depth, int index) {
	/*final ArrayList<CompileError> copy = new ArrayList<>(causes);*/
	/*copy.sort(Comparator.comparingInt(CompileError::depth));*/
	/*StringBuilder joiner = new StringBuilder();*/
	/*for (int i = 0;*/
	/*i < copy.size();*/
	/*i++) {
			CompileError error = copy.get(i);
			String format = error.format(depth + 1, i);
			joiner.append(format);
		}*/
	/*final String formattedChildren = joiner.toString();*/
	/*final String s = depth == 0 ? "" : System.lineSeparator() + "\t".repeat(depth);*/
	/*return s + index + ") " + reason + ": " + context.display(depth) + formattedChildren;*/
}
int depth_CompileError() {
	/*return 1 + causes.stream().mapToInt(CompileError::depth).max().orElse(0);*/
}
