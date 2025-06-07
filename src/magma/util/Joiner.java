package magma.util;

public class Joiner implements Collector<String, Option<String>> {
    private final String delimiter;

    public Joiner() {
        this("");
    }

    public Joiner(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public Option<String> createInitial() {
        return new None<>();
    }

    @Override
    public Option<String> fold(Option<String> current, String element) {
        return new Some<>(current.map(inner -> inner + delimiter + element).orElse(element));
    }
}
