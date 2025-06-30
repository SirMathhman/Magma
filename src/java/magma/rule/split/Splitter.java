package magma.rule.split;

import magma.api.Tuple;

import java.util.Optional;

public interface Splitter {
    Optional<Tuple<String, String>> split(String input);
}
