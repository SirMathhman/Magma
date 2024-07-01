package magma.build.compile.parse.rule;

import magma.build.compile.parse.result.ParsingResult;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class Rules {
    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(1);

    public static Optional<Integer> wrapIndex(int index) {
        return index == -1 ? Optional.empty() : Optional.of(index);
    }

    public static ParsingResult toNode(Rule rule, String input) {
        var before = LocalDateTime.now();
        var result = rule.toNode(input);
        var after = LocalDateTime.now();
        var duration = Duration.between(before, after);
        return result.withDuration(duration);
    }

    public static boolean exceedsTimeout(Duration duration) {
        return duration.compareTo(DEFAULT_TIMEOUT) > 0;
    }
}
