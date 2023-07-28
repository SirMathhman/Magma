package com.meti.app.compile.block;

import com.meti.app.Attribute;
import com.meti.app.compile.Node;
import com.meti.app.compile.Renderer;
import com.meti.core.Option;
import com.meti.iterate.Iterators;
import com.meti.java.JavaString;
import com.meti.java.List;
import com.meti.java.String_;

import static com.meti.java.JavaString.fromSlice;

public record BlockRenderer(Node block) implements Renderer {
    private static String_ renderContent(List<? extends Node> content) {
        return content.iter()
                .map(node -> node.apply(fromSlice("value")).flatMap(Attribute::asString))
                .flatMap(Iterators::fromOption)
                .map(line -> line.prepend("\t"))
                .collect(JavaString.joining(fromSlice("")))
                .unwrapOrElse(fromSlice(""))
                .prepend("{\n")
                .append("}\n");
    }

    @Override
    public Option<String_> render() {
        return block.apply(fromSlice("lines")).flatMap(Attribute::asListOfNodes).map(BlockRenderer::renderContent);
    }
}