package magma.compile.rule;

import magma.list.Stream;

import java.util.regex.Pattern;

public record DelimitedRule(String delimiter) implements Divider {
	@Override
	public Stream<Slice> divide(Slice slice) {
		return slice.split(Pattern.quote(delimiter)).stream();
	}
}
