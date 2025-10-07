// Generated transpiled C++ from 'src\main\java\magma\compile\rule\KeepLastMerger.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct KeepLastMerger {};
Option<Tuple<String, String>> merge_KeepLastMerger(List<String> segments, String delimiter) {
	if (/*???*/)return new_???();
	String left=segments.subListOrEmpty(/*???*/, /*???*/).stream().collect(new_???(delimiter));
	Option<String> lastOpt=segments.getLast();
	if (/*???*/)return new_???(new_???(left, right));
	return new_???();
}
