// Generated transpiled C++ from 'src\main\java\magma\compile\rule\DividingSplitter.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct DividingSplitter {/*???*/ divider;/*???*/ merger;};
/*???*/ DividingSplitter_DividingSplitter(/*???*/ divider) {
	this(divider, new_???());
}
/*???*/ KeepFirst_DividingSplitter(/*???*/ divider) {
	return new_???(divider, new_???());
}
/*???*/ KeepLast_DividingSplitter(/*???*/ divider) {
	return new_???(divider, new_???());
}
/*???*/ split_DividingSplitter(/*???*/ input) {
	/*???*/ segments=divider.divide(input).toList();
	/*???*/ delimiter=divider.delimiter();
	return merger.merge(segments, delimiter);
}
/*???*/ createErrorMessage_DividingSplitter() {
	return "";
}
/*???*/ merge_DividingSplitter(/*???*/ left, /*???*/ right) {
	/*???*/ right;
}
