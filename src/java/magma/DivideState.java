package magma;

import java.util.Optional;
import java.util.stream.Stream;

public interface DivideState {
    DivideState advance();

    DivideState append(char c);

    Stream<String> stream();

    DivideState enter();

    DivideState exit();

    boolean isLevel();

    Optional<Tuple<DivideState, Character>> pop();
}
