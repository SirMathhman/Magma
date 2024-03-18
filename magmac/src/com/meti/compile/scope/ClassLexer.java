package com.meti.compile.scope;

import com.meti.collect.Range;
import com.meti.collect.Tuple;
import com.meti.collect.option.Option;
import com.meti.collect.stream.Collectors;
import com.meti.compile.Lexer;
import com.meti.compile.TypeCompiler;
import com.meti.compile.node.Content;
import com.meti.compile.node.MapNode;
import com.meti.compile.node.Node;
import com.meti.java.JavaString;

import static com.meti.collect.option.Options.$$;
import static com.meti.collect.option.Options.$Option;

public record ClassLexer(JavaString stripped) implements Lexer {
    @Override
    public Option<Node> lex() {
        return $Option(() -> {
            var bodyStart = stripped.firstIndexOfChar('{').$();
            var bodySlices = stripped.sliceAt(bodyStart);
            var beforeContent = bodySlices.a();
            var content = bodySlices.b();

            var extendsRange = beforeContent.firstRangeOfSlice("extends ");
            var args = beforeContent.sliceTo(extendsRange.map(Range::startIndex).orElse(bodyStart))
                    .strip()
                    .split(" ")
                    .collect(Collectors.toList());

            var nameList = args.popLast().$();
            var flags = nameList.b();
            var name = nameList.a();

            if(!flags.contains(new JavaString("class"))) $$();

            var contentOutput = new Content(content, 0);

            var builder = MapNode.Builder(new JavaString("class"))
                    .withListOfStrings("flags", flags)
                    .withString("name", name)
                    .withNode("value", contentOutput);

            var withSuperClass = extendsRange.flatMap(range -> range.endIndex().to(bodyStart))
                    .map(stripped::sliceBetween)
                    .map(JavaString::strip)
                    .flatMap(superClassString -> new TypeCompiler(superClassString).compile())
                    .map(superClassName -> builder.withString("extends", superClassName))
                    .orElse(builder);

            return withSuperClass.complete();
        });
    }
}