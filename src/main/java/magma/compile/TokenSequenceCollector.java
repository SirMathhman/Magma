package magma.compile;

import magma.compile.rule.TokenSequence;
import magma.list.Collector;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

public record TokenSequenceCollector(String delimiter) implements Collector<TokenSequence, Option<TokenSequence>> {
	@Override
	public Option<TokenSequence> initial() {
		return new None<TokenSequence>();
	}

	@Override
	public Option<TokenSequence> fold(Option<TokenSequence> current, TokenSequence element) {
		return switch (current) {
			case None<TokenSequence> _ -> new Some<TokenSequence>(element);
			case Some<TokenSequence> some -> new Some<>(some.value().appendSlice(delimiter).appendSequence(element));
		};
	}
}
