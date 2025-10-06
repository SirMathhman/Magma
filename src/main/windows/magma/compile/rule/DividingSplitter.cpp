// Generated transpiled C++ from 'src\main\java\magma\compile\rule\DividingSplitter.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct DividingSplitter {Divider divider;Merger merger;};
public DividingSplitter_DividingSplitter(Divider divider) {
	this((divider, new_KeepFirstMerger(());
}
DividingSplitter KeepFirst_DividingSplitter(Divider divider) {
	return new_DividingSplitter((divider, new_KeepFirstMerger(());
}
DividingSplitter KeepLast_DividingSplitter(Divider divider) {
	return new_DividingSplitter((divider, new_KeepLastMerger(());
}
@Override
	public Option<> split_DividingSplitter(char* input) {
	final List<> segments=divider.divide(input).toList();
	char* delimiter=divider.delimiter();
	return merger.merge(segments, delimiter);
}
char* createErrorMessage_DividingSplitter() {
	segments found.";
}
char* merge_DividingSplitter(char* left, char* right) {
	return left+divider.delimiter()+right;
}
