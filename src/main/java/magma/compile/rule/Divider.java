package magma.compile.rule;

import java.util.stream.Stream;

public interface Divider {
	Stream<String> divide(String input);

	String delimiter();
}
