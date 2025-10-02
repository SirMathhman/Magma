// Generated transpiled C++ from 'src\main\java\magma\compile\rule\DividingSplitter.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct DividingSplitter {/*Divider*/ divider;/* Merger*/ merger;};
/*private*/ DividingSplitter_DividingSplitter(/*Divider*/ divider) {
	/*this*/(/*divider*/, /* new KeepFirstMerger())*/;
}
/*public static DividingSplitter*/ KeepFirst_DividingSplitter(/*Divider*/ divider) {
	/*return new DividingSplitter*/(/*divider*/, /* new KeepFirstMerger())*/;
}
/*public static DividingSplitter*/ KeepLast_DividingSplitter(/*Divider*/ divider) {
	/*return new DividingSplitter*/(/*divider*/, /* new KeepLastMerger())*/;
}
/*@Override
	public Option<Tuple<String, String>>*/ split_DividingSplitter(/*String*/ input) {
	/*final List<String> segments */=/* divider.divide(input).toList()*/;
	/*final String delimiter */=/* divider.delimiter()*/;
	/*return merger.merge*/(/*segments*/, /* delimiter)*/;
}
/*@Override
	public String*/ createErrorMessage_DividingSplitter() {
	return /*"No segments found."*/;
}
/*@Override
	public String*/ merge_DividingSplitter(/*String*/ left, /* String*/ right) {
	/*return left + divider.delimiter*/(/*) + right*/;
}
