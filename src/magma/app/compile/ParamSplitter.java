package magma.app.compile;

import java.util.ArrayList;
import java.util.List;

public class ParamSplitter implements Splitter {
    @Override
    public List<String> split(String input) {
        var list = new ArrayList<String>();
        var buffer = new StringBuilder();
        var length = input.length();
        for (int i = 0; i < length; i++) {
            var c = input.charAt(i);
            if (c == ',') {
                if (!buffer.isEmpty()) list.add(buffer.toString());
                buffer = new StringBuilder();
            } else {
                buffer.append(c);
            }
        }
        if (!buffer.isEmpty()) list.add(buffer.toString());
        return list;
    }
}
