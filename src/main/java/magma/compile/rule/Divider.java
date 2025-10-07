package magma.compile.rule;

import magma.list.Stream;

public interface Divider {
	Stream<Slice> divide(Slice slice);

	String delimiter();
}
