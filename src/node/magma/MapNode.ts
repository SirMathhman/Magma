/*import java.util.HashMap;*/
/*import java.util.Map;*/
/*import java.util.Optional;*/
/*import java.util.stream.Stream;*/
class MapNode implements Node {/*
    private final Map<String, String> strings = new HashMap<>();

    @Override
    public Node withString(final String key, final String value) {
        this.strings.put(key, value);
        return this;
    }

    @Override
    public Optional<String> findString(final String key) {
        return Optional.ofNullable(this.strings.get(key));
    }

    @Override
    public Stream<Tuple<String, String>> streamStrings() {
        return this.strings.entrySet().stream().map(entry -> new Tuple<>(entry.getKey(), entry.getValue()));
    }

    @Override
    public Node merge(final Node other) {
        return other.streamStrings()
                    .<Node>reduce(this, (node, tuple) -> node.withString(tuple.left(), tuple.right()),
                                  (_, next) -> next);
    }
*/}
