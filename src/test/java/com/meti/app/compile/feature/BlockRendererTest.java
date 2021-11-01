package com.meti.app.compile.feature;

import com.meti.app.compile.CompileException;
import com.meti.app.compile.node.output.CompoundOutput;
import com.meti.app.compile.node.output.EmptyOutput;
import com.meti.app.compile.node.output.NodeOutput;
import com.meti.app.compile.node.output.StringOutput;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockRendererTest {

    @Test
    void renderDefined() throws CompileException {
        var line = new Return(new IntegerNode(420));
        var expected = new CompoundOutput(List.of(
                new StringOutput("{"),
                new NodeOutput(line),
                new StringOutput("}")));
        var actual = new BlockRenderer(new Block(List.of(line))).process()
                .orElse(new EmptyOutput());
        assertEquals(expected, actual);
    }
}