// Generated transpiled C++ from 'src\main\java\magma\compile\rule\DividingSplitter.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<>
struct DividingSplitter{Divider divider;, Merger merger;};
template<>
public DividingSplitter_DividingSplitter(Divider divider) {/*
		this(divider, new KeepFirstMerger());
	*/}
template<>
/*public static DividingSplitter*/ keepFirst_DividingSplitter(Divider divider) {/*
		return new DividingSplitter(divider, new KeepFirstMerger());
	*/}
template<>
/*public static DividingSplitter*/ keepLast_DividingSplitter(Divider divider) {/*
		return new DividingSplitter(divider, new KeepLastMerger());
	*/}
template<>
@Override
	public Option<Tuple<String, String>> split_DividingSplitter(char* input) {/*
		final List<String> segments = divider.divide(input).toList();
		final String delimiter = divider.delimiter();

		return merger.merge(segments, delimiter);
	*/}
