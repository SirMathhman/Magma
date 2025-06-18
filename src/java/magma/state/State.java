package magma.state;

import java.util.List;

public interface State {
    State append(char c);

    State advance();

    List<String> segments();

    boolean isLevel();

    State enter();

    State exit();
}
