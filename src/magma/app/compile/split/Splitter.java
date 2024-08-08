package magma.app.compile.split;

import java.util.List;

public interface Splitter {
    String computeDelimiter();

    List<String> split(String input);
}
