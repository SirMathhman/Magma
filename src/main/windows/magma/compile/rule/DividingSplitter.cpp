// Generated transpiled C++ from 'src\main\java\magma\compile\rule\DividingSplitter.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<>
struct DividingSplitter{Divider divider;, Merger merger;};
template<>
public DividingSplitter_DividingSplitter(Divider divider) {/*
		this(divider, new KeepFirstMerger());
	*/}
template<>
DividingSplitter keepFirst_DividingSplitter(Divider divider) {/*
		return new DividingSplitter(divider, new KeepFirstMerger());
	*/}
template<>
DividingSplitter KeepLast_DividingSplitter(Divider divider) {/*
		return new DividingSplitter(divider, new KeepLastMerger());
	*/}
template<>
Option<Tuple<String, String>> split_DividingSplitter(char* input) {/*
		final List<String> segments = divider.divide(input).toList();
		final String delimiter = divider.delimiter();

		return merger.merge(segments, delimiter);
	*/}
template<>
char* createErrorMessage_DividingSplitter() {/*
		return "No segments found.";
	*/}
template<>
char* merge_DividingSplitter(char* left, char* right) {/*
		return left + divider.delimiter() + right;
	*/}
