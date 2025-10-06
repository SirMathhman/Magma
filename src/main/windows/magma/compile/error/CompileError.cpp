// Generated transpiled C++ from 'src\main\java\magma\compile\error\CompileError.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct CompileError {/*???*/ reason;/*???*/ context;List<> causes;};
/*???*/ CompileError_CompileError(/*???*/ reason, /*???*/ sourceCode) {
	this(reason, sourceCode, Collections.emptyList());
}
/*???*/ display_CompileError() {
	return format(/*???*/, new_???());
}
/*???*/ format_CompileError(/*???*/ depth, List<> indices) {
	List<> copy=causes.copy();
	copy.sort(Comparator.comparingInt(/*???*/));
	/*???*/ formattedChildren=joinErrors(depth, indices, copy);
	/*???*/ s;
	if (depth==/*???*/)s="";
	else
	s==System.lineSeparator()+"".repeat(depth);
	/*???*/ joinedIndices=getCollect(indices);
	return s+joinedIndices+""+reason+""+context.display(depth)+formattedChildren;
}
/*???*/ getCollect_CompileError(List<> indices) {
	Stream<> stream=indices.stream();
	Stream<> stringStream=stream.map(/*???*/);
	return stringStream.collect(new_???(""));
}
/*???*/ joinErrors_CompileError(/*???*/ depth, List<> indices, List<> copy) {
	Stream<> range=Stream.range(/*???*/, copy.size());
	Stream<> stringStream=range.map(/*???*/(depth, copy, indices, index));
	return stringStream.collect(new_???(""));
}
/*???*/ formatChild_CompileError(/*???*/ depth, List<> copy, List<> indices, /*???*/ last) {
	/*???*/ error=copy.getOrNull(last);
	indices.push(last);
	/*???*/ format=error.format(depth+/*???*/, indices);
	indices.pop();
	/*???*/ format;
}
/*???*/ depth_CompileError() {
	Stream<> intStream=causes.stream().map(/*???*/);
	Option<> max=intStream.collect(new_???());
	return /*???*/+max.orElse(/*???*/);
}
