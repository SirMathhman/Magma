package magma;

public interface Result<T, E> {
	class Ok<T, E> implements Result<T, E> {
		private final T value;

		public Ok(T value) {
			this.value = value;
		}

		@Override
		public boolean isOk() {
			return true;
		}

		@Override
		public boolean isErr() {
			return false;
		}

		@Override
		public T unwrap() {
			return value;
		}

		@Override
		public E unwrapErr() {
			throw new RuntimeException("Called unwrapErr on Ok value");
		}
	}

	class Err<T, E> implements Result<T, E> {
		private final E error;

		public Err(E error) {
			this.error = error;
		}

		@Override
		public boolean isOk() {
			return false;
		}

		@Override
		public boolean isErr() {
			return true;
		}

		@Override
		public T unwrap() {
			throw new RuntimeException("Called unwrap on Err value");
		}

		@Override
		public E unwrapErr() {
			return error;
		}
	}

	static <T, E> Result<T, E> ok(T value) {
		return new Ok<>(value);
	}

	static <T, E> Result<T, E> err(E error) {
		return new Err<>(error);
	}

	boolean isOk();

	boolean isErr();

	T unwrap();

	E unwrapErr();
}