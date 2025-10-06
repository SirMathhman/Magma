// Generated transpiled C++ from 'src\main\java\magma\compile\rule\KeepLastMerger.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct KeepLastMerger {};
@Override
	public Option<> merge_KeepLastMerger(List<> segments, char* delimiter) {
	if (/*???*/)new None<>();
	char* left=String.join(delimiter, segments.subList(0, segments.size() - 1));
	char* right=segments.getLast();
	return new_Some_((new_Tuple_((left, right));
}
