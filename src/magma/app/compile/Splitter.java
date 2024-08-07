package magma.app.compile;

import java.util.List;

public interface Splitter {
    String computeDelimiter();

    List<String> split(String input);
}
