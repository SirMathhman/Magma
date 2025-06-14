package magma.app.compile.rule.divide.state;

import java.util.stream.Stream;

public interface DivideState {
    DivideState advance();

    DivideState append(char c);

    Stream<String> segments();
}
