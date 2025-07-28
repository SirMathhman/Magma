package magma.result;

import java.util.Optional;

public record Ok<T, X>(T value) implements Result<T, X> {
	@Override
	public Optional<T> findValue() {
		return Optional.of(this.value);
	}
}
