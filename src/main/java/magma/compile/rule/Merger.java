package magma.compile.rule;

import magma.Tuple;
import magma.option.Option;

import java.util.List;

public interface Merger {
	Option<Tuple<String, String>> merge(List<String> segments, String delimiter);
}
