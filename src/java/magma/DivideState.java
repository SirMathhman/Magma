package magma;

import java.util.Optional;
import java.util.stream.Stream;

public interface DivideState {
    Stream<String> stream();

    DivideState advance();

    DivideState append(char c);

    boolean isLevel();

    DivideState enter();

    DivideState exit();

    Optional<Tuple<DivideState, Character>> pop();
}
