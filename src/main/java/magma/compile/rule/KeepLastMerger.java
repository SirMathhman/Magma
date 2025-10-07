package magma.compile.rule;

import magma.Tuple;
import magma.list.Joiner;
import magma.list.List;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

public class KeepLastMerger implements Merger {
	@Override
	public Option<Tuple<TokenSequence, TokenSequence>> merge(List<TokenSequence> segments, String delimiter) {
		if (segments.size() < 2) return new None<Tuple<TokenSequence, TokenSequence>>();

		// Join all but last element
		final TokenSequence left = new StringTokenSequence(segments.subListOrEmpty(0, segments.size() - 1)
																														 .stream()
																														 .map(TokenSequence::value)
																														 .collect(new Joiner(delimiter)));

		final Option<TokenSequence> lastOpt = segments.getLast();
		if (lastOpt instanceof Some<TokenSequence>(TokenSequence right))
			return new Some<Tuple<TokenSequence, TokenSequence>>(new Tuple<TokenSequence, TokenSequence>(left, right));
		return new None<Tuple<TokenSequence, TokenSequence>>();
	}
}
