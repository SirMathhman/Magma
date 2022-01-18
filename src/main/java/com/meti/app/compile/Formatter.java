package com.meti.app.compile;

import com.meti.app.compile.attribute.AttributeException;
import com.meti.app.compile.node.Node;

public interface Formatter {
    Node apply(Node node) throws AttributeException;
}