package magma.list;

public record Joiner(String delimiter) implements Collector<String, String> {
	@Override
	public String initial() {
		return "";
	}

	@Override
	public String fold(String current, String element) {
		if (current.isEmpty()) return element;
		return current + delimiter + element;
	}
}
