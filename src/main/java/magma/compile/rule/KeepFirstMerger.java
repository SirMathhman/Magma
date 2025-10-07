package magma.compile.rule;

import magma.Tuple;
import magma.list.Joiner;
import magma.list.List;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

public class KeepFirstMerger implements Merger {
	@Override
	public Option<Tuple<TokenSequence, TokenSequence>> merge(List<TokenSequence> segments, String delimiter) {
		if (segments.size() < 2) return new None<Tuple<TokenSequence, TokenSequence>>();

		// Split into first segment and the rest
		final TokenSequence left = segments.getFirst().orElse(null);
		final TokenSequence right = new StringTokenSequence(segments.subListOrEmpty(1, segments.size())
																															.stream()
																															.map(TokenSequence::value)
																															.collect(new Joiner(delimiter)));

		return new Some<Tuple<TokenSequence, TokenSequence>>(new Tuple<TokenSequence, TokenSequence>(left, right));
	}
}
