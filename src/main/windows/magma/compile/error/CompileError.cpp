// Generated transpiled C++ from 'src\main\java\magma\compile\error\CompileError.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct CompileError {char* reason;Context context;List<> causes;};
public CompileError_CompileError(char* reason, Context sourceCode) {
	this(reason, sourceCode, Collections.emptyList());
}
char* display_CompileError() {
	return format(0, 0);
}
char* format_CompileError(int depth, int index) {
	new ArrayList<>(causes);
	copy.sort(Comparator.comparingInt(CompileError::depth));
	char* formattedChildren=joinErrors(depth, copy);
	char* s;
	if (depth==0)s="";
	else s=System.lineSeparator()+"".repeat(depth);
	return s+index+""+reason+""+context.display(depth)+formattedChildren;
}
char* joinErrors_CompileError(int depth, List<> copy) {
	return IntStream.range(0, copy.size()).mapToObj(index -> formatChild(depth, copy, index)).collect(Collectors.joining());
}
char* formatChild_CompileError(int depth, List<> copy, int i) {
	CompileError error=copy.get(i);
	return error.format(depth + 1, i);
}
int depth_CompileError() {
	return 1+causes.stream().mapToInt(CompileError::depth).max().orElse(0);
}
