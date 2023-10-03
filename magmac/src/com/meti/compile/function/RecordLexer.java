package com.meti.compile.function;

import com.meti.api.collect.JavaString;
import com.meti.api.collect.Range;
import com.meti.api.option.Option;
import com.meti.compile.Lexer;
import com.meti.compile.node.Content;
import com.meti.compile.node.MapNode;
import com.meti.compile.node.Node;

import static com.meti.api.option.Options.$Option;

public record RecordLexer(JavaString stripped) implements Lexer {
    @Override
    public Option<Node> lex() {
        return $Option(() -> {
            var nameStart = stripped().firstIndexOfSlice("record ").$()
                    .nextBy("record ".length())
                    .$();

            var paramStart = stripped().firstIndexOfChar('(').$();
            var bodyStart = stripped().firstIndexOfChar('{').$();
            var bodyEnd = stripped().lastIndexOfChar('}').$()
                    .next()
                    .$();

            Range range1 = nameStart.to(paramStart).$();
            var name = this.stripped().sliceBetween(range1);
            Range range = bodyStart.to(bodyEnd).$();
            var bodySlice = this.stripped().sliceBetween(range);

            return MapNode.Builder(JavaString.apply("method"))
                    .withString(JavaString.apply("name"), name)
                    .withNode("block", new Content(bodySlice))
                    .complete();
        });
    }
}