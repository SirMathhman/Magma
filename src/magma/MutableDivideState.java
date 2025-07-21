package magma;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

public final class MutableDivideState implements DivideState {
	private final Collection<String> segments = new ArrayList<>();
	private final StringBuilder buffer = new StringBuilder();

	@Override
	public Stream<String> stream() {
		return this.segments.stream();
	}

	@Override
	public DivideState advance() {
		this.segments.add(this.buffer.toString());
		this.buffer.delete(0, this.buffer.length());
		return this;
	}

	@Override
	public DivideState append(final char c) {
		this.buffer.append(c);
		return this;
	}
}