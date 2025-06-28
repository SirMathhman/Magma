package magma;

record Joiner(CharSequence delimiter) implements Collector<String, Optional<String>> {
    public Joiner() {
        this("");
    }

    @Override
    public Optional<String> createInitial() {
        return new None<>();
    }

    @Override
    public Optional<String> fold(final Optional<String> current, final String s) {
        return new Some<>(current.map(inner -> inner + this.delimiter + s).orElse(""));
    }
}
