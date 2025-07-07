package magma;

import java.util.stream.Stream;

public interface DivideState {
    Stream<String> stream();

    DivideState append(char c);

    DivideState advance();
}
