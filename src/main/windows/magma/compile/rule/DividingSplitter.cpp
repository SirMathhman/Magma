// Generated transpiled C++ from 'src\main\java\magma\compile\rule\DividingSplitter.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct DividingSplitter {Divider divider;Merger merger;};
private DividingSplitter_DividingSplitter(Divider divider) {
	/*this(divider, new KeepFirstMerger());*/
}
DividingSplitter KeepFirst_DividingSplitter(Divider divider) {
	/*return new DividingSplitter(divider, new KeepFirstMerger());*/
}
DividingSplitter KeepLast_DividingSplitter(Divider divider) {
	/*return new DividingSplitter(divider, new KeepLastMerger());*/
}
Option<Tuple<String, String>> split_DividingSplitter(char* input) {
	/*final List<String> segments = divider.divide(input).toList();*/
	/*final String delimiter = divider.delimiter();*/
	/*return merger.merge(segments, delimiter);*/
}
char* createErrorMessage_DividingSplitter() {
	/*return "No segments found.";*/
}
char* merge_DividingSplitter(char* left, char* right) {
	/*return left + divider.delimiter() + right;*/
}
