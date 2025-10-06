// Generated transpiled C++ from 'src\main\java\magma\compile\rule\KeepFirstMerger.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct KeepFirstMerger {};
Option<> merge_KeepFirstMerger(List<> segments, /*???*/ delimiter) {
	if (/*???*/)return new_???();
	/*???*/ left=segments.getFirst().orElse(null);
	/*???*/ right=segments.subListOrEmpty(/*???*/, segments.size()).stream().collect(new_???(delimiter));
	return new_???(new_???(left, right));
}
