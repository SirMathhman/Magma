package magma.compile.rule;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public record DelimitedRule(String delimiter) implements Divider {
	@Override
	public Stream<String> divide(String input) {
		return Arrays.stream(input.split(Pattern.quote(delimiter)));
	}
}
