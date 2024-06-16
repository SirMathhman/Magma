package magma.compile.lang;

import magma.compile.rule.EmptyRule;
import magma.compile.rule.LazyRule;
import magma.compile.rule.NumberRule;
import magma.compile.rule.OrRule;
import magma.compile.rule.Rule;
import magma.compile.rule.SymbolRule;
import magma.compile.rule.TypeRule;
import magma.compile.rule.split.FirstRule;
import magma.compile.rule.split.LastRule;
import magma.compile.rule.split.MembersSplitter;
import magma.compile.rule.split.ParamSplitter;
import magma.compile.rule.split.SplitMultipleRule;
import magma.compile.rule.split.SplitOnceRule;
import magma.compile.rule.text.LeftRule;
import magma.compile.rule.text.RightRule;
import magma.compile.rule.text.StripRule;
import magma.compile.rule.text.extract.ExtractNodeRule;
import magma.compile.rule.text.extract.ExtractStringRule;
import magma.compile.rule.text.extract.SimpleExtractStringListRule;

import java.util.List;
import java.util.Optional;

public class Lang {
    static Rule createBlock(Rule child) {
        return new TypeRule("block", new SplitMultipleRule(new MembersSplitter(), "", "children", new StripRule(child)));
    }

    static TypeRule createImportRule(TypeRule namespace) {
        return new TypeRule("import", new LeftRule("import ", new RightRule(new ExtractNodeRule("external", namespace), ";")));
    }

    static TypeRule createNamespaceRule() {
        return new TypeRule("namespace", new SimpleExtractStringListRule("namespace", "."));
    }

    static TypeRule createCatchRule(Rule definition, Rule statement) {
        var declaration = new StripRule(new LeftRule("(", new RightRule(definition, ")")));
        var value = new ExtractNodeRule("child", createBlock(statement));

        var afterKeyword = new FirstRule(new ExtractNodeRule("condition", new TypeRule("declaration", declaration)), "{", new RightRule(value, "}"));
        return new TypeRule("catch", new LeftRule("catch ", afterKeyword));
    }

    static LazyRule createTypeRule() {
        var type = new LazyRule();
        var children = new SplitMultipleRule(new ParamSplitter(), ", ", "children", new StripRule(type));
        var generic = new TypeRule("generic", new FirstRule(new ExtractNodeRule("parent", type), "<", new RightRule(children, ">")));

        type.setRule(new OrRule(List.of(
                new TypeRule("array", new RightRule(new ExtractNodeRule("child", type), "[]")),
                generic,
                new TypeRule("symbol", new SymbolRule(new ExtractStringRule("value"))),
                new TypeRule("access", new LastRule(new ExtractNodeRule("parent", type), ".", new ExtractStringRule("member")))
        )));

        return type;
    }

    static TypeRule createTryRule(Rule statement) {
        return new TypeRule("try", new LeftRule("try ", new StripRule(new LeftRule("{", new RightRule(new ExtractNodeRule("child", createBlock(statement)), "}")))));
    }

    static StripRule createModifiersRule() {
        return new StripRule(new SimpleExtractStringListRule("modifiers", " "));
    }

    static TypeRule createInvocationRule(Rule value) {
        var arguments = new OrRule(List.of(
                new SplitMultipleRule(new ParamSplitter(), ", ", "arguments", new StripRule(value))
        ));

        var caller = new ExtractNodeRule("caller", new StripRule(value));
        return new TypeRule("invocation", new RightRule(new InvocationStart(caller, arguments), ")"));
    }

    static TypeRule createCommentRule() {
        return new TypeRule("comment", new LeftRule("//", new ExtractStringRule("value")));
    }

    static TypeRule createIfRule(String type, Rule value, Rule statement) {
        var child = new ExtractNodeRule("condition", value);
        var conditionParent = createScope("condition", new StripRule(new LeftRule("(", child)));

        var valueWithBlock = new LeftRule("{", new RightRule(new ExtractNodeRule("value", createBlock(statement)), "}"));
        var valueWithoutBlock = new ExtractNodeRule("value", statement);

        var valueParent = createScope("value", new StripRule(new OrRule(List.of(
                valueWithBlock,
                valueWithoutBlock
        ))));

        return new TypeRule(type, new LeftRule(type, new ConditionEndRule(conditionParent, valueParent)));
    }

    public static Rule createScope(String name, Rule rule) {
        var withScope = name + ":scope";
        var type = new TypeRule(withScope, rule);
        return new ExtractNodeRule(withScope, type);
    }

    static Rule createReturnRule(Rule value) {
        var maybeChild = new OrRule(List.of(new LeftRule(" ", new ExtractNodeRule("child", new StripRule(value))), new EmptyRule()));
        var after = new OrRule(List.of(new RightRule(maybeChild, ";"), maybeChild));
        return new TypeRule("return", new LeftRule("return", after));
    }

    static Rule createForRule(Rule definition, Rule value, Rule statement, String delimiter) {
        var collection = new StripRule(new ExtractNodeRule("collection", value));
        var condition = new RightRule(new LastRule(new StripRule(definition), delimiter, collection), ")");
        var content = new RightRule(new ExtractNodeRule("child", createBlock(statement)), "}");
        var child = new StripRule(new LeftRule("(", condition));
        var leftRule = new ExtractNodeRule("condition-parent", new TypeRule("condition-parent", child));
        var after = new FirstRule(leftRule, "{", content);
        return new TypeRule("for", new LeftRule("for", after));
    }

    static Rule createElseRule(Rule statement) {
        var child = new RightRule(new ExtractNodeRule("child", createBlock(statement)), "}");
        return new TypeRule("else", new LeftRule("else", new StripRule(new LeftRule("{", child))));
    }

    static Rule createAssignmentRule(Rule value) {
        var left = new ExtractNodeRule("assignable", new TypeRule("assignable-parent", new StripRule(new SymbolRule(new ExtractStringRule("reference")))));
        var right = new RightRule(new ExtractNodeRule("value", new StripRule(value)), ";");
        return new TypeRule("assignment", new FirstRule(left, "=", right));
    }

    static TypeRule createDeclarationRule(Rule definition, Rule value) {
        var left = new ExtractNodeRule("definition", new TypeRule("definition", new StripRule(definition)));
        var right = new RightRule(new ExtractNodeRule("value", new StripRule(value)), ";");
        return new TypeRule("declaration", new FirstRule(left, "=", right));
    }

    static Rule createParamsRule(Rule definition) {
        return new SplitMultipleRule(new ParamSplitter(), ", ", "params", new StripRule(new TypeRule("definition", definition)));
    }

    static TypeRule createStringRule() {
        return new TypeRule("string", new LeftRule("\"", new RightRule(new ExtractStringRule("value"), "\"")));
    }

    static TypeRule createAccessRule(LazyRule value) {
        var parent = new ExtractNodeRule("parent", new StripRule(value));
        return new TypeRule("access", new LastRule(parent, ".", new StripRule(new SymbolRule(new ExtractStringRule("child")))));
    }

    static TypeRule createSymbolRule() {
        return new TypeRule("symbol", new SymbolRule(new ExtractStringRule("value")));
    }

    static TypeRule createTernaryRule(LazyRule value) {
        return new TypeRule("ternary", new FirstRule(
                new StripRule(new ExtractNodeRule("condition", value)), "?",
                new FirstRule(
                        new StripRule(new ExtractNodeRule("true", value)), ":",
                        new StripRule(new ExtractNodeRule("false", value)))));
    }

    static TypeRule createOperatorRule(String name, String slice, Rule value) {
        return new TypeRule(name, new SplitOnceRule(new StripRule(new ExtractNodeRule("leftRule", value)), slice, new StripRule(new ExtractNodeRule("right", value))) {
            @Override
            protected Optional<Integer> computeIndex(String input) {
                var depth = 0;
                for (int i = 0; i < input.length(); i++) {
                    var maybeSlice = input.substring(i, Math.min(i + slice.length(), input.length()));
                    if (maybeSlice.equals(slice) && depth == 0) {
                        return Optional.of(i);
                    } else {
                        var c = input.charAt(i);
                        if (c == '(') depth++;
                        if (c == ')') depth--;
                    }
                }

                /*
                TODO: find the operator
                 */

                return Optional.empty();
            }
        });
    }

    static TypeRule createNumberRule() {
        return new TypeRule("number", new NumberRule(new ExtractStringRule("value")));
    }

    static TypeRule createCharRule() {
        return new TypeRule("char", new LeftRule("'", new RightRule(new ExtractStringRule("value"), "'")));
    }

    static TypeRule createEmptyStatementRule() {
        return new TypeRule("empty", new RightRule(new StripRule(new EmptyRule()), ";"));
    }

    static Rule createThrowRule(Rule value) {
        var after = new RightRule(createScope("value", new ExtractNodeRule("value", value)), ";");
        return new TypeRule("throw", new LeftRule("throw ", after));
    }

    static TypeRule createNotRule(LazyRule value) {
        return new TypeRule("not", new LeftRule("!", new ExtractNodeRule("child", value)));
    }
}
