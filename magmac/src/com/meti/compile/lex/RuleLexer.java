package com.meti.compile.lex;

import com.meti.api.collect.ImmutableLists;
import com.meti.api.collect.JavaString;
import com.meti.api.option.None;
import com.meti.api.option.Option;
import com.meti.compile.Lexer;
import com.meti.compile.RuleLexerFactory;
import com.meti.compile.node.Content;
import com.meti.compile.node.MapNode;
import com.meti.compile.node.Node;

import static com.meti.api.option.Options.$Option;

public class RuleLexer implements Lexer {
    private final JavaString input;
    private final JavaString actualType;
    private final Rule rule;
    private final String requiredType;

    public RuleLexer(JavaString input, JavaString actualType, Rule rule, String requiredType) {
        this.input = input;
        this.actualType = actualType;
        this.requiredType = requiredType;
        this.rule = rule;
    }

    public static RuleLexer createDeclarationLexer(JavaString input, JavaString type) {
        return new RuleLexerFactory("definition", ConjunctionRule.of(
                ContentRule.of(JavaString.apply("type")),
                ValueRule.of(JavaString.apply(" ")),
                TextRule.of(JavaString.apply("name"))
        )).createDeclarationLexer(input, type);
    }

    private static MapNode.Builder attachText(RuleResult result, MapNode.Builder builder) {
        return result.getText().iter().foldRight(builder, (builder1, stringJavaStringTuple) -> builder1.withString(stringJavaStringTuple.a(), stringJavaStringTuple.b()));
    }

    private static MapNode.Builder attachContent(RuleResult result, MapNode.Builder withStrings) {
        return result.getContent().iter().foldRight(withStrings, (builder1, stringJavaStringTuple) -> builder1.withContent(stringJavaStringTuple.a(), stringJavaStringTuple.b()));
    }

    private static MapNode.Builder getAttachLists(RuleResult result, MapNode.Builder withContent) {
        return result.getListOfStrings().iter().foldRight(withContent, (builder2, stringListTuple) -> builder2.withNodeList(stringListTuple.a(), stringListTuple.b().iter()
                .map((JavaString value) -> new Content(value, JavaString.apply(stringListTuple.a())))
                .collect(ImmutableLists.into())));
    }

    @Override
    public Option<Node> lex() {
        if (!actualType.equalsToSlice(requiredType)) {
            return None.apply();
        }

        return $Option(() -> {
            var result = rule.extract(input).$();
            var builder = MapNode.Builder(actualType);
            var withStrings = attachText(result, builder);
            var withContent = attachContent(result, withStrings);
            var withLists = getAttachLists(result, withContent);
            return withLists.complete();
        });
    }
}
