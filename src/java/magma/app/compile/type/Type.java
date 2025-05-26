package magma.app.compile.type;

import magma.app.compile.node.Node;

public interface Type extends Node {
    String generate();

    String generateBeforeName();

    String generateSimple();
}
