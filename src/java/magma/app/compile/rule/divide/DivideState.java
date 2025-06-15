package magma.app.compile.rule.divide;

import java.util.List;

public interface DivideState {
    DivideState advance();

    DivideState append(char c);

    List<String> segments();
}
