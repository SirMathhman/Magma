package magma.compile.rule.divide;

import jvm.collect.list.Lists;
import magma.collect.list.List_;

public record CharDivider(char delimiter) implements Divider {
    @Override
    public List_<String> divide(String input) {
        List_<String> requestedNamespace = Lists.empty();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == delimiter()) {
                requestedNamespace = requestedNamespace.add(buffer.toString());
                buffer = new StringBuilder();
            } else {
                buffer.append(c);
            }
        }

        requestedNamespace = requestedNamespace.add(buffer.toString());
        return requestedNamespace;
    }

    @Override
    public String join(String current, String element) {
        if (current.isEmpty()) return element;
        return current + delimiter + element;
    }
}