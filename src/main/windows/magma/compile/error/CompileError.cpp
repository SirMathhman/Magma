// Generated transpiled C++ from 'src\main\java\magma\compile\error\CompileError.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct CompileError {char* reason;Context context;List<> causes;};
public CompileError_CompileError(char* reason, Context sourceCode) {
	this(reason, sourceCode, Collections.emptyList());
}
char* display_CompileError() {
	return format(0, new_???());
}
char* format_CompileError(int depth, Stack<> indices) {
	ArrayList<> copy=new_???(causes);
	copy.sort(Comparator.comparingInt(/*???*/));
	char* formattedChildren=joinErrors(depth, indices, copy);
	char* s;
	if (depth==0)s="";
	else s=System.lineSeparator()+"".repeat(depth);
	char* joinedIndices=getCollect(indices);
	return s+joinedIndices+""+reason+""+context.display(depth)+formattedChildren;
}
char* getCollect_CompileError(Stack<> indices) {
	Stream<> stream=indices.stream();
	Stream<> stringStream=stream.map(/*???*/);
	return stringStream.collect(Collectors.joining(""));
}
char* joinErrors_CompileError(int depth, Stack<> indices, List<> copy) {
	IntStream range=IntStream.range(0, copy.size());
	Stream<> stringStream=range.mapToObj(/*???*/(depth, copy, indices, index));
	return stringStream.collect(Collectors.joining());
}
char* formatChild_CompileError(int depth, List<> copy, Stack<> indices, int last) {
	CompileError error=copy.get(last);
	indices.push(last);
	char* format=error.format(depth+1, indices);
	indices.pop();
	return format;
}
int depth_CompileError() {
	IntStream intStream=causes.stream().mapToInt(/*???*/);
	OptionalInt max=intStream.max();
	return 1+max.orElse(0);
}
