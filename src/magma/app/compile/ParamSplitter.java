package magma.app.compile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParamSplitter implements Splitter {
    @Override
    public String computeDelimiter() {
        return ", ";
    }

    @Override
    public List<String> split(String input) {
        var list = new ArrayList<String>();
        var buffer = new StringBuilder();
        var length = input.length();
        var queue = IntStream.range(0, length)
                .mapToObj(input::charAt)
                .collect(Collectors.toCollection(LinkedList::new));

        var depth = 0;
        while (!queue.isEmpty()) {
            var c = queue.pop();

            if (c == '\"') {
                buffer.append(c);
                while (!queue.isEmpty()) {
                    var next = queue.pop();
                    buffer.append(next);
                    if (next == '\"') {
                        break;
                    }
                }
                continue;
            }

            if (c == ',' && depth == 0) {
                if (!buffer.isEmpty()) list.add(buffer.toString());
                buffer = new StringBuilder();
            } else {
                if (c == '(' || c == '<' || c == '{') depth++;
                if (c == ')' || c == '>' || c == '}') depth--;
                buffer.append(c);
            }
        }

        if (!buffer.isEmpty()) list.add(buffer.toString());
        return list;
    }
}
