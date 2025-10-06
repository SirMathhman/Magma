package magma.compile.rule;

import magma.Tuple;
import magma.list.List;
import magma.option.Option;

public interface Merger {
	Option<Tuple<String, String>> merge(List<String> segments, String delimiter);
}
