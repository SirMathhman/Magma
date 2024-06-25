package magma.api.json;

import magma.api.Tuple;
import magma.api.collect.stream.Collectors;
import magma.api.option.None;
import magma.api.option.Option;
import magma.java.JavaMap;

public record JSONObjectParser(JSONParser valueParser) implements JSONParser {
    @Override
    public Option<JSONValue> parse(String input) {
        if (!input.startsWith("{") || !input.endsWith("}")) return new None<>();
        return JSON.split(input.substring(1, input.length() - 1))
                .stream()
                .map(this::parseEntry)
                .collect(Collectors.required(JavaMap.collecting()))
                .map(JSONObject::new);
    }

    private Option<Tuple<String, JSONValue>> parseEntry(String inner) {
        var separator = inner.indexOf(':');
        if(separator == -1) return new None<>();

        var left = inner.substring(0, separator).strip();
        var right = inner.substring(separator + 1).strip();

        if (!left.startsWith("\"") || !left.endsWith("\"")) {
            return new None<>();
        }

        var key = left.substring(1, left.length() - 1);
        return valueParser.parse(right).map(value -> new Tuple<>(key, value));
    }
}
