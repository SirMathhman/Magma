package magma.compile.rule;

import magma.Tuple;
import magma.list.List;
import magma.option.Option;

public interface Merger {
	Option<Tuple<TokenSequence, TokenSequence>> merge(List<TokenSequence> segments, String delimiter);
}
