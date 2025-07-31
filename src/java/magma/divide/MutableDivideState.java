package magma.divide;

import magma.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public class MutableDivideState implements DivideState {
	private final String input;
	private final Collection<String> segments;
	private final StringBuilder buffer;
	private int index;
	private int braceCount;

	public MutableDivideState(final String input) {
		this.input = input;
		this.index = 0;
		this.braceCount = 0;
		this.segments = new ArrayList<>();
		this.buffer = new StringBuilder();
	}

	@Override
	public final Optional<Tuple<DivideState, Character>> pop() {
		final var length = this.input.length();
		if (this.index >= length) return Optional.empty();
		final char c = this.input.charAt(this.index);

		this.index = this.index + 1;
		return Optional.of(new Tuple<>(this, c));
	}

	@Override
	public final boolean isLevel() {
		return 0 == this.braceCount;
	}

	@Override
	public final DivideState exit() {
		this.braceCount = this.braceCount - 1;
		return this;
	}

	@Override
	public final DivideState enter() {
		this.braceCount = this.braceCount + 1;
		return this;
	}

	@Override
	public final DivideState advance() {
		this.segments.add(this.buffer.toString());
		this.buffer.setLength(0);
		return this;
	}

	@Override
	public DivideState append(final char c) {
		this.buffer.append(c);
		return this;
	}

	@Override
	public final Stream<String> stream() {
		return this.segments.stream();
	}
}
