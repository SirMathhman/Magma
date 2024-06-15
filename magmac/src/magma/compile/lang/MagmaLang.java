package magma.compile.lang;

import magma.compile.rule.EmptyRule;
import magma.compile.rule.LazyRule;
import magma.compile.rule.OrRule;
import magma.compile.rule.Rule;
import magma.compile.rule.TypeRule;
import magma.compile.rule.split.FirstRule;
import magma.compile.rule.split.LastRule;
import magma.compile.rule.split.ParamSplitter;
import magma.compile.rule.split.SplitMultipleRule;
import magma.compile.rule.text.LeftRule;
import magma.compile.rule.text.RightRule;
import magma.compile.rule.text.StripRule;
import magma.compile.rule.text.extract.ExtractNodeRule;
import magma.compile.rule.text.extract.ExtractStringRule;
import magma.compile.rule.text.extract.SimpleExtractStringListRule;

import java.util.List;

public class MagmaLang {
    public static Rule createRootRule() {
        var statement = new LazyRule();

        var function = new TypeRule("function", new LastRule(new SimpleExtractStringListRule("modifiers", " "), " ",
                new FirstRule(new ExtractStringRule("name"), "() => {", new RightRule(new ExtractNodeRule("child", Lang.createBlock(statement)), "}"))));


        var value = new LazyRule();

        var arguments = new OrRule(List.of(
                new SplitMultipleRule(new ParamSplitter(), ", ", "arguments", new StripRule(value)),
                new EmptyRule()
        ));

        var caller = new ExtractNodeRule("caller", value);
        var invocation = new TypeRule("invocation", new RightRule(new InvocationStart(caller, arguments), ")"));
        value.setRule(new OrRule(List.of(
                new TypeRule("string", new LeftRule("\"", new RightRule(new ExtractStringRule("value"), "\""))),
                invocation,
                new TypeRule("ternary", new FirstRule(
                        new StripRule(new ExtractNodeRule("condition", value)), "?",
                        new FirstRule(
                                new StripRule(new ExtractNodeRule("true", value)), ":",
                                new StripRule(new ExtractNodeRule("false", value))))),
                new TypeRule("symbol", new ExtractStringRule("value")),
                new TypeRule("access", new LastRule(new ExtractNodeRule("parent", value), ".", new ExtractStringRule("child"))),
                new TypeRule("equals", new LeftRule("? == ?", new EmptyRule()))
        )));

        var name = new ExtractStringRule("name");
        var child = new LastRule(new SimpleExtractStringListRule("modifiers", " "), " ", name);
        var declaration = new TypeRule("declaration", new FirstRule(new OrRule(List.of(child, name)), " = ", new RightRule(new ExtractNodeRule("value", value), ";")));

        var child1 = new RightRule(new ExtractNodeRule("child", Lang.createBlock(statement)), "}");

        statement.setRule(new OrRule(List.of(
                declaration,
                new TypeRule("try", new LeftRule("try {", child1)),
                new TypeRule("catch", new LeftRule("catch (?){", child1)),
                new TypeRule("invocation", new RightRule(invocation, ";")),
                new TypeRule("if", new LeftRule("if (?){", child1)),
                new TypeRule("else", new LeftRule("else {", child1)),
                new TypeRule("for", new LeftRule("for (?){", child1)),
                new TypeRule("return", new LeftRule("return ", new EmptyRule())),
                new TypeRule("comment", new LeftRule("//", new ExtractStringRule("value"))),
                new TypeRule("assignment", new RightRule(new EmptyRule(), "? = ?")),
                function
        )));


        return Lang.createBlock(new OrRule(List.of(
                Lang.createImportRule(Lang.createNamespaceRule()),
                statement)));
    }
}
