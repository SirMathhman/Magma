// Generated transpiled C++ from 'src\main\java\magma\compile\rule\KeepLastMerger.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct KeepLastMerger {};
Option<> merge_KeepLastMerger(List<> segments, /*???*/ delimiter) {
	if (/*???*/)return new_???();
	/*???*/ left=segments.subListOrEmpty(/*???*/, /*???*/).stream().collect(new_???(delimiter));
	Option<> lastOpt=segments.getLast();
	if (/*???*/)return new_???(new_???(left, right));
	return new_???();
}
