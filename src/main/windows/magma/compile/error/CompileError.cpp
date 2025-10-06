// Generated transpiled C++ from 'src\main\java\magma\compile\error\CompileError.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct CompileError {char* reason;Context context;List<> causes;};
public CompileError_CompileError(char* reason, Context sourceCode) {
	this((reason, sourceCode, Collections.emptyList());
}
char* display_CompileError() {
	return format((0, new_Stack_(());
}
char* format_CompileError(int depth, Stack<> indices) {
	new ArrayList<>(causes);
	copy.sort((Comparator.comparingInt(CompileError::depth));
	char* formattedChildren=joinErrors((depth, indices, copy);
	char* s;
	if (depth==0)s="";
	else s=System.lineSeparator()+"".repeat(depth);
	char* joinedIndices=indices.stream().map(String::valueOf).collect(Collectors.joining("."));
	return s+joinedIndices+""+reason+""+context.display(depth)+formattedChildren;
}
char* joinErrors_CompileError(int depth, Stack<> indices, List<> copy) {
	return IntStream.range(0, copy.size()).mapToObj(index -> formatChild(depth, copy, indices, index)).collect(Collectors.joining());
}
char* formatChild_CompileError(int depth, List<> copy, Stack<> indices, int last) {
	CompileError error=copy.get(last);
	indices.push((last);
	char* format=error.format(depth + 1, indices);
	indices.pop(();
	return format;
}
int depth_CompileError() {
	return 1+causes.stream().mapToInt(CompileError::depth).max().orElse(0);
}
