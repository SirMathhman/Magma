package magma.compile.rule;

import java.util.List;

public class ParamSplitter implements Splitter {
    @Override
    public List<String> split(String input) {
        return List.of(input.split(","));
    }
}
