// Generated transpiled C++ from 'src\main\java\magma\compile\error\CompileError.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct CompileError {/*???*/ reason;/*???*/ context;List<> causes;};
/*???*/ CompileError_CompileError(/*???*/ reason, /*???*/ sourceCode) {
	this(reason, sourceCode, Collections.emptyList());
}
/*???*/ display_CompileError() {
	return format(/*???*/, new_???());
}
/*???*/ format_CompileError(/*???*/ depth, Stack<> indices) {
	ArrayList<> copy=new_???(causes);
	copy.sort(Comparator.comparingInt(/*???*/));
	/*???*/ formattedChildren=joinErrors(depth, indices, copy);
	/*???*/ s;
	if (depth==/*???*/)s="";
	/*???*/ s=System.lineSeparator()+"".repeat(depth);
	/*???*/ joinedIndices=getCollect(indices);
	return s+joinedIndices+""+reason+""+context.display(depth)+formattedChildren;
}
/*???*/ getCollect_CompileError(Stack<> indices) {
	Stream<> stream=indices.stream();
	Stream<> stringStream=stream.map(/*???*/);
	return stringStream.collect(Collectors.joining(""));
}
/*???*/ joinErrors_CompileError(/*???*/ depth, Stack<> indices, List<> copy) {
	/*???*/ range=IntStream.range(/*???*/, copy.size());
	Stream<> stringStream=range.mapToObj(/*???*/(depth, copy, indices, index));
	return stringStream.collect(Collectors.joining());
}
/*???*/ formatChild_CompileError(/*???*/ depth, List<> copy, Stack<> indices, /*???*/ last) {
	/*???*/ error=copy.get(last);
	indices.push(last);
	/*???*/ format=error.format(depth+/*???*/, indices);
	indices.pop();
	/*???*/ format;
}
/*???*/ depth_CompileError() {
	/*???*/ intStream=causes.stream().mapToInt(/*???*/);
	/*???*/ max=intStream.max();
	return /*???*/+max.orElse(/*???*/);
}
