// Generated transpiled C++ from 'src\main\java\magma\compile\rule\KeepFirstMerger.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct KeepFirstMerger {};
/*@Override
	public Option<Tuple<String, String>>*/ merge_KeepFirstMerger(/*List<String>*/ segments, /* String*/ delimiter) {
	/*if */(/*segments.size() < 2) return new None<>()*/;
	// Split into first segment and the rest
	/*final String left */=/* segments.getFirst()*/;
	/*final String right */=/* String.join(delimiter, segments.subList(1, segments.size()))*/;
	/*return new Some<>*/(/*new Tuple<>(left*/, /* right))*/;
}
