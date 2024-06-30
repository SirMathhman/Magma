package magma.build.compile.parse.rule.split;

import java.util.Optional;

public interface Searcher {
    Optional<Integer> search(String input);
}
