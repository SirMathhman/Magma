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
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> name =
                new StripRule(new IdentifierRule(new StringRule("name")));

        final var withTypeParameters =
                new SuffixRule(SplitRule.First(name, "<", new StringRule("type-parameters")), ">");
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>>
                maybeWithTypeParams = new OrRule(List.of(new StripRule(withTypeParameters), name));

        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>>
                params = new StringRule("params");
        final var withParameters =
                SplitRule.First(SplitRule.First(maybeWithTypeParams, "(", params), ")", new StringRule("more"));
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>>
                maybeWithParameters = new OrRule(List.of(withParameters, maybeWithTypeParams));

        final var withSuperClass = SplitRule.Last(maybeWithParameters, "extends ", JavaLang.createTypeRule());
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>>
                maybeWithSuperClass = new OrRule(List.of(withSuperClass, maybeWithParameters));

        final var withImplementing = SplitRule.Last(maybeWithSuperClass, "implements", JavaLang.createTypeRule());
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>>
                maybeWithImplements = new OrRule(List.of(withImplementing, maybeWithSuperClass));

        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> modifiers =
                new StringRule("modifiers");
        return SplitRule.First(modifiers, type + " ", maybeWithImplements);
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createNamespaceRule(final String type) {
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> child =
                new StringRule("child");

        final var discard = new OrRule(List.of(SplitRule.Last(new StringRule("discard"), ".", child), child));
        return new TypeRule<>(type, new StripRule(new SuffixRule(new PrefixRule(type + " ", discard), ";")),
                              JavaLang.FACTORY);
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createTypeRule() {
        return new OrRule(List.of(JavaLang.createGenericRule(),
                                  new TypeRule<>("identifier", new StringRule("value"), JavaLang.FACTORY)));
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createGenericRule() {
        return new TypeRule<>("generic", new StripRule(
                new SuffixRule(SplitRule.First(new StringRule("base"), "<", new StringRule("value")), ">")),
                              JavaLang.FACTORY);
    }
}
