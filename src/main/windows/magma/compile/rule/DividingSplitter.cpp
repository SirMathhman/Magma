// Generated transpiled C++ from 'src\main\java\magma\compile\rule\DividingSplitter.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct DividingSplitter {Divider divider;Merger merger;};
public DividingSplitter_DividingSplitter(Divider divider) {
	this(divider, new_???());
}
DividingSplitter KeepFirst_DividingSplitter(Divider divider) {
	return new_???(divider, new_???());
}
DividingSplitter KeepLast_DividingSplitter(Divider divider) {
	return new_???(divider, new_???());
}
Option<Tuple<String, String>> split_DividingSplitter(String input) {
	List<String> segments=divider.divide(input).toList();
	String delimiter=divider.delimiter();
	return merger.merge(segments, delimiter);
}
String createErrorMessage_DividingSplitter() {
	return "";
}
String merge_DividingSplitter(String left, String right) {
	return left+divider.delimiter()+right;
}
