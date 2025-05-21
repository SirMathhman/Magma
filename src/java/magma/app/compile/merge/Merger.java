package magma.app.compile.merge;

import magma.api.collect.list.Iterable;

public interface Merger {
    static String generateAll(Iterable<String> elements, Merger merger) {
        return elements.iter().foldWithInitial("", merger::merge);
    }

    String merge(String s, String s2);
}
