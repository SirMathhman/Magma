package magma.lang;

import magma.compile.result.ResultFactory;
import magma.compile.result.ResultFactoryImpl;
import magma.error.FormatError;
import magma.node.EverythingNode;
import magma.node.result.NodeResult;
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
        return new OrRule(List.of(JavaLang.createImportRule(), JavaLang.createStructureRule("class"),
                                  JavaLang.createStructureRule("interface"), JavaLang.createStructureRule("record"),
                                  JavaLang.createPlaceholderRule()));
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createPlaceholderRule() {
        return new TypeRule<>("placeholder", new StringRule("value"), JavaLang.FACTORY);
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createStructureRule(final String type) {
        final var anImplements = JavaLang.createStructureHeaderRule(type);

        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> content =
                new StringRule("content");
        return new StripRule(new SuffixRule(SplitRule.First(anImplements, "{", content), "}"));
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createStructureHeaderRule(
            final String type) {
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> modifiers =
                new StringRule("modifiers");

        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> name =
                new StripRule(new StringRule("name"));

        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> params =
                new StringRule("params");

        final var withParams = SplitRule.First(name, "(", params);
        final var afterKeyword = SplitRule.First(withParams, ")", new StringRule("more"));
        final var rightRule = new OrRule(List.of(afterKeyword, name));
        final var header = new TypeRule<>(type, SplitRule.First(modifiers, type + " ", rightRule), JavaLang.FACTORY);

        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> header1 =
                new OrRule(List.of(SplitRule.Last(header, " extends ", JavaLang.createTypeRule()), header));
        return new OrRule(List.of(SplitRule.Last(header1, "implements", JavaLang.createTypeRule()), header1));
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createImportRule() {
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> child =
                new StringRule("child");

        return new TypeRule<>("import", new StripRule(
                new SuffixRule(new PrefixRule("import ", SplitRule.Last(new StringRule("discard"), ".", child)), ";")),
                              JavaLang.FACTORY);
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createTypeRule() {
        return new StripRule(
                new SuffixRule(SplitRule.First(new StringRule("base"), "<", new StringRule("value")), ">"));
    }

}
