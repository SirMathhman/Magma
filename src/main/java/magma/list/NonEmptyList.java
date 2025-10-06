package magma.list;

public interface NonEmptyList<T> {
	class MyNonEmptyListImpl<T> implements NonEmptyList<T> {
		private final List<T> list;

		public MyNonEmptyListImpl(List<T> list) {this.list = list;}

		@Override
		public Stream<T> stream() {
			return list.stream();
		}
	}

	static <T> NonEmptyList<T> from(List<T> list) {
		return new MyNonEmptyListImpl<T>(list);
	}

	Stream<T> stream();
}
