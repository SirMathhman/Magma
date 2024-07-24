package magma.lang;

import magma.JavaList;

import java.util.List;

public class MagmaLang {
    public static String generateFunction(Node node) {
        return node.findString(Node.MODIFIERS).orElse("") + "def " + node.findString(Node.NAME).orElse("") + "(" +
               node.findString(Node.PARAMS).orElse("") +
               ") => {" + generateBlock(node.findStringList(Node.CONTENT).orElse(new JavaList<>()).list(), node.findInteger(Node.DEPTH).orElse(0)) + "}";
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
