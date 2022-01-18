package com.meti.app.compile.common.variable;

import com.meti.api.option.Option;
import com.meti.api.option.Some;
import com.meti.app.compile.lex.Lexer;
import com.meti.app.compile.node.Node;
import com.meti.app.compile.node.Text;

public record VariableLexer(Text text) implements Lexer {
    @Override
    public Option<Node> lex() {
        return new Some<>(new Variable(text));
    }
}