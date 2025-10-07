// Generated transpiled C++ from 'src\main\java\magma\compile\error\CompileError.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct CompileError {String reason;Context context;List<CompileError> causes;};
public CompileError_CompileError(String reason, Context sourceCode) {
	this(reason, sourceCode, Collections.emptyList());
}
String display_CompileError() {
	return format(/*???*/, new_???());
}
String format_CompileError(int depth, List<Integer> indices) {
	List<CompileError> copy=causes.copy();
	copy.sort(Comparator.comparingInt(/*???*/));
	String formattedChildren=joinErrors(depth, indices, copy);
	String s;
	if (depth==/*???*/)s="";
	else
	s==System.lineSeparator()+"".repeat(depth);
	String joinedIndices=getCollect(indices);
	return s+joinedIndices+""+reason+""+context.display(depth)+formattedChildren;
}
String getCollect_CompileError(List<Integer> indices) {
	Stream<Integer> stream=indices.stream();
	Stream<String> stringStream=stream.map(/*???*/);
	return stringStream.collect(new_???(""));
}
String joinErrors_CompileError(int depth, List<Integer> indices, List<CompileError> copy) {
	Stream<Integer> range=Stream.range(/*???*/, copy.size());
	Stream<String> stringStream=range.map(/*???*/(depth, copy, indices, index));
	return stringStream.collect(new_???(""));
}
String formatChild_CompileError(int depth, List<CompileError> copy, List<Integer> indices, int last) {
	CompileError error=copy.get(last).orElse(null);
	indices.addLast(last);
	String format=error.format(depth+/*???*/, indices);
	indices.removeLast();
	return format;
}
int depth_CompileError() {
	Stream<Integer> intStream=causes.stream().map(/*???*/);
	Option<Integer> max=intStream.collect(new_???());
	return /*???*/+max.orElse(/*???*/);
}
