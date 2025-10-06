package magma.compile.rule;

import magma.list.ArrayHead;
import magma.list.HeadedStream;
import magma.list.Stream;

import java.util.regex.Pattern;

public record DelimitedRule(String delimiter) implements Divider {
	@Override
	public Stream<String> divide(String input) {
		return new HeadedStream<>(new ArrayHead<>(input.split(Pattern.quote(delimiter))));
	}
}
