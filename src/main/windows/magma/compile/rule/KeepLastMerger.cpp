// Generated transpiled C++ from 'src\main\java\magma\compile\rule\KeepLastMerger.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct KeepLastMerger {};
Option<Tuple<String, String>> merge_KeepLastMerger(List<String> segments, char* delimiter) {
	if (/*segments.size() < 2)*/
	return /*new None<>()*/;
	// Join all but last element
	/*final String left = String.join*/(/*delimiter*/, /* segments.subList(0*/, /* segments.size() - 1))*/;
	/*final String right = segments.getLast*/(/*)*/;
	return /*new Some<>(new Tuple<>(left, right))*/;
}
