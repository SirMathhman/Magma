// Generated transpiled C++ from 'src\main\java\magma\compile\rule\KeepFirstMerger.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct KeepFirstMerger {};
@Override
	public Option<> merge_KeepFirstMerger(List<> segments, char* delimiter) {
	if (/*???*/)new None<>();
	char* left=segments.getFirst();
	char* right=String.join(delimiter, segments.subList(1, segments.size()));
	return new_Some_((new_Tuple_((left, right));
}
