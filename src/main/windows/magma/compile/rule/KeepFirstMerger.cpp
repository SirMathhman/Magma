// Generated transpiled C++ from 'src\main\java\magma\compile\rule\KeepFirstMerger.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct KeepFirstMerger {};
Option<> merge_KeepFirstMerger(List<> segments, char* delimiter) {
	if (/*???*/)return new_???();
	char* left=segments.getFirst();
	char* right=String.join(delimiter, segments.subList(/*???*/, segments.size()));
	return new_???(new_???(left, right));
}
