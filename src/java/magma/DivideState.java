package magma;

import java.util.stream.Stream;

public interface DivideState {
    DivideState advance();

    DivideState append(char c);

    Stream<String> stream();

    DivideState enter();

    DivideState exit();

    boolean isLevel();
}
