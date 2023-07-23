package com.meti.app.compile;

import com.meti.app.Attribute;
import com.meti.app.NodeListAttribute;
import com.meti.core.None;
import com.meti.core.Option;
import com.meti.java.List;
import com.meti.java.Set;
import com.meti.java.String_;

import static com.meti.java.JavaString.fromSlice;

public interface Node {
    default Option<Attribute> apply(String_ key) {
        if (key.equalsTo(fromSlice("lines"))) {
            return lines().map(NodeListAttribute::new);
        } else if (key.equalsTo(fromSlice("type"))) {
            return type().map(NodeAttribute::new);
        } else {
            return None.apply();
        }
    }

    Option<List<? extends Node>> lines();

    Option<Node> type();

    default Option<Node> withLines(List<? extends Node> lines) {
        return None.apply();
    }

    default Option<String_> value() {
        return None.apply();
    }

    default Option<Node> withBody(Node body) {
        return None.apply();
    }

    default Option<Node> body() {
        return None.apply();
    }

    default Option<String_> name() {
        return None.apply();
    }

    default Option<Set<? extends Node>> parameters() {
        return None.apply();
    }

    default Option<Node> withReturns(Node returns) {
        return None.apply();
    }

    default Option<Set<String_>> keywords() {
        return None.apply();
    }

    default Option<String_> parent() {
        return None.apply();
    }

    default Option<String_> child() {
        return None.apply();
    }

    default Option<Node> returns() {
        return None.apply();
    }

    default Option<Node> withParameters(Set<? extends Node> parameters) {
        return None.apply();
    }
}
