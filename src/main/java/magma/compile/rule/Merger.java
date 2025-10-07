package magma.compile.rule;

import magma.Tuple;
import magma.list.List;
import magma.option.Option;

public interface Merger {
	Option<Tuple<Slice, Slice>> merge(List<Slice> segments, String delimiter);
}
