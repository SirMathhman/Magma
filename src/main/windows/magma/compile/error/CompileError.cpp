// Generated transpiled C++ from 'src\main\java\magma\compile\error\CompileError.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct CompileError {};
public CompileError_CompileError() {
	this();
}
String display_CompileError() {
	return format();
}
String format_CompileError() {
	List<CompileError> copy=causes.copy();
	copy.sort();
	String formattedChildren=joinErrors();
	String s;
	if (depth==/*???*/)
	{
	s="";}
	else
	s==System.lineSeparator()+"".repeat();
	String joinedIndices=getCollect();
	return s+joinedIndices+""+reason+""+context.display()+formattedChildren;
}
String getCollect_CompileError() {
	Stream<Integer> stream=indices.stream();
	Stream<String> stringStream=stream.map();
	return stringStream.collect();
}
String joinErrors_CompileError() {
	Stream<Integer> range=Stream.range();
	Stream<String> stringStream=range.map();
	return stringStream.collect();
}
String formatChild_CompileError() {
	CompileError error=copy.get().orElse();
	indices.addLast();
	String format=error.format();
	indices.removeLast();
	return format;
}
int depth_CompileError() {
	Stream<Integer> intStream=causes.stream().map();
	Option<Integer> max=intStream.collect();
	return /*???*/+max.orElse();
}
