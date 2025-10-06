// Generated transpiled C++ from 'src\main\java\magma\transform\RootTransformer.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct RootTransformer {};
Result<> transform_RootTransformer(/*???*/ node) {
	List<> children=node.children();
	Stream<> stream=children.stream();
	Stream<> listStream=stream.map(/*???*/);
	Stream<> cRootSegmentStream=listStream.flatMap(/*???*/);
	List<> newChildren=cRootSegmentStream.toList();
	return new_???(new_???(newChildren));
}
