package magma.lang;

import magma.JavaList;

import java.util.List;

public class MagmaLang {
    public static String generateFunction(int depth, String modifiers, String name, JavaList<String> content) {
        return modifiers + "def " + name + "() => {" + generateBlock(content.list(), depth) + "}";
    }

    public static String generateBlock(List<String> compiledSegments, int depth) {
        var builder = new StringBuilder();
        for (int i = 0; i < compiledSegments.size(); i++) {
            var segment = compiledSegments.get(i);
            String prefix;
            var isFirstStatement = i == 0 && depth == 0;
            if (isFirstStatement) prefix = "";
            else prefix = "\n" + "\t".repeat(depth);
            builder.append(prefix).append(segment);
        }
        return builder + "\n" + "\t".repeat(depth == 0 ? 0 : depth - 1);
    }
}
