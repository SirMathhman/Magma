// Generated transpiled C++ from 'src\main\java\magma\compile\rule\KeepFirstMerger.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct KeepFirstMerger {};
Option<Tuple<String, String>> merge_KeepFirstMerger(List<String> segments, String delimiter) {
	if (/*???*/)return new_???();
	String left=segments.getFirst().orElse(null);
	String right=segments.subListOrEmpty(/*???*/, segments.size()).stream().collect(new_???(delimiter));
	return new_???(new_???(left, right));
}
