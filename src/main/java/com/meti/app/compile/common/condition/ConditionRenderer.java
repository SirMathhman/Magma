package com.meti.app.compile.common.condition;

import com.meti.api.option.None;
import com.meti.api.option.Option;
import com.meti.api.option.Some;
import com.meti.app.compile.attribute.Attribute;
import com.meti.app.compile.attribute.AttributeException;
import com.meti.app.compile.node.Node;
import com.meti.app.compile.node.Text;
import com.meti.app.compile.render.Renderer;

public record ConditionRenderer(Node node) implements Renderer {
    @Override
    public Option<Text> render() throws AttributeException {
        if (node.is(Node.Type.If)) {
            var arguments = node.apply(Attribute.Type.Arguments).asNode()
                    .apply(Attribute.Type.Value).asText();
            var value = this.node.apply(Attribute.Type.Value).asNode()
                    .apply(Attribute.Type.Value).asText();
            return new Some<>(new Text("if(" + arguments.compute() + ")" + value.compute()));
        }
        return new None<>();
    }
}