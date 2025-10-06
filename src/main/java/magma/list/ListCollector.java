package magma.list;

public record ListCollector<T>() implements Collector<T, List<T>> {
	@Override
	public List<T> initial() {
		return new ArrayList<T>();
	}

	@Override
	public List<T> fold(List<T> current, T element) {
		return current.addLast(element);
	}
}
