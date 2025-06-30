package magma;

import java.util.Optional;
import java.util.stream.Stream;

public interface DivideState {
    Stream<String> stream();

    Optional<Character> pop();

    DivideState append(char c);

    DivideState advance();
}
