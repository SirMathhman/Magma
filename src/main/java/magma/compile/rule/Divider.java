package magma.compile.rule;

import magma.list.Stream;

public interface Divider {
	Stream<String> divide(String input);

	String delimiter();
}
