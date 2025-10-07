// Generated transpiled C++ from 'src\main\java\magma\compile\rule\DividingSplitter.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct DividingSplitter {};
public DividingSplitter_DividingSplitter() {
	this();
}
DividingSplitter KeepFirst_DividingSplitter() {
	return new_???();
}
DividingSplitter KeepLast_DividingSplitter() {
	return new_???();
}
Option<Tuple<TokenSequence, TokenSequence>> split_DividingSplitter() {
	var segments=divider.divide().toList();
	var delimiter=divider.delimiter();
	return merger.merge();
}
String createErrorMessage_DividingSplitter() {
	return "";
}
String merge_DividingSplitter() {
	return left+divider.delimiter()+right;
}
