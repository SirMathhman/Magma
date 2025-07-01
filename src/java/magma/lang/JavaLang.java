package magma.lang;

import magma.compile.result.ResultFactory;
import magma.compile.result.ResultFactoryImpl;
import magma.error.FormatError;
import magma.node.EverythingNode;
import magma.node.result.NodeResult;
import magma.rule.IdentifierRule;
import magma.rule.OrRule;
import magma.rule.PrefixRule;
import magma.rule.Rule;
import magma.rule.SplitRule;
import magma.rule.StringRule;
import magma.rule.StripRule;
import magma.rule.SuffixRule;
import magma.rule.TypeRule;
import magma.string.result.StringResult;

import java.util.List;

public class JavaLang {
    private static final ResultFactory<StringResult<FormatError>> FACTORY = ResultFactoryImpl.createResultFactory();

    private JavaLang() {}

    public static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createJavaRootSegmentRule() {
        return new OrRule(List.of(JavaLang.createNamespaceRule("package"), JavaLang.createNamespaceRule("import"),
                                  JavaLang.createStructureRule("class"), JavaLang.createStructureRule("interface"),
                                  JavaLang.createStructureRule("record")));
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createStructureRule(final String type) {
        final var anImplements = JavaLang.createStructureHeaderRule(type);

        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> content =
                new StringRule("content");
        return new TypeRule<>(type, new StripRule(new SuffixRule(SplitRule.First(anImplements, "{", content), "}")),
                              JavaLang.FACTORY);
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createStructureHeaderRule(
            final String type) {
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> modifiers =
                new StringRule("modifiers");

        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> name =
                new StripRule(new IdentifierRule(new StringRule("name")));

        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> params =
                new StringRule("params");

        final var withParams = SplitRule.First(name, "(", params);
        final var afterKeyword = SplitRule.First(withParams, ")", new StringRule("more"));
        final var rightRule = new OrRule(List.of(afterKeyword, name));
        final var header = SplitRule.First(modifiers, type + " ", rightRule);

        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> header1 =
                new OrRule(List.of(SplitRule.Last(header, " extends ", JavaLang.createTypeRule()), header));
        return new OrRule(List.of(SplitRule.Last(header1, "implements", JavaLang.createTypeRule()), header1));
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createNamespaceRule(final String type) {
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> child =
                new StringRule("child");

        return new TypeRule<>(type, new StripRule(
                new SuffixRule(new PrefixRule(type + " ", SplitRule.Last(new StringRule("discard"), ".", child)), ";")),
                              JavaLang.FACTORY);
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createTypeRule() {
        return new StripRule(
                new SuffixRule(SplitRule.First(new StringRule("base"), "<", new StringRule("value")), ">"));
    }

}
