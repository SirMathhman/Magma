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
    private static final ResultFactory<EverythingNode, StringResult<FormatError>> FACTORY = ResultFactoryImpl.get();

    private JavaLang() {}

    public static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createJavaRootSegmentRule() {
        return new OrRule<EverythingNode>(List.of(JavaLang.createNamespaceRule("package"), JavaLang.createNamespaceRule("import"),
                                                  JavaLang.createStructureRule("class"), JavaLang.createStructureRule("interface"),
                                                  JavaLang.createStructureRule("record")), ResultFactoryImpl.get());
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createStructureRule(final String type) {
        final var anImplements = JavaLang.createStructureHeaderRule(type);

        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> content =
                new StringRule("content");
        return new TypeRule<>(type, new StripRule(new SuffixRule<EverythingNode>(SplitRule.First(anImplements, "{", content), "}",
                                                                                 ResultFactoryImpl.get())),
                              JavaLang.FACTORY);
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createStructureHeaderRule(
            final String type) {
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> name =
                new StripRule(new IdentifierRule<EverythingNode>(new StringRule("name"), ResultFactoryImpl.get()));

        final var withTypeParameters =
                new SuffixRule<EverythingNode>(SplitRule.First(name, "<", new StringRule("type-parameters")), ">",
                                               ResultFactoryImpl.get());
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>>
                maybeWithTypeParams = new OrRule<EverythingNode>(List.of(new StripRule(withTypeParameters), name),
                                                                 ResultFactoryImpl.get());

        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>>
                params = new StringRule("params");
        final var withParameters =
                SplitRule.First(SplitRule.First(maybeWithTypeParams, "(", params), ")", new StringRule("more"));
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>>
                maybeWithParameters = new OrRule<EverythingNode>(List.of(withParameters, maybeWithTypeParams), ResultFactoryImpl.get());

        final var withSuperClass = SplitRule.Last(maybeWithParameters, "extends ", JavaLang.createTypeRule());
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>>
                maybeWithSuperClass = new OrRule<EverythingNode>(List.of(withSuperClass, maybeWithParameters), ResultFactoryImpl.get());

        final var withImplementing = SplitRule.Last(maybeWithSuperClass, "implements", JavaLang.createTypeRule());
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>>
                maybeWithImplements = new OrRule<EverythingNode>(List.of(withImplementing, maybeWithSuperClass),
                                                                 ResultFactoryImpl.get());

        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> modifiers =
                new StringRule("modifiers");
        return SplitRule.First(modifiers, type + " ", maybeWithImplements);
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createNamespaceRule(final String type) {
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> child =
                new StringRule("child");

        final var discard = new OrRule<EverythingNode>(List.of(SplitRule.Last(new StringRule("discard"), ".", child), child),
                                                       ResultFactoryImpl.get());
        return new TypeRule<>(type, new StripRule(new SuffixRule<EverythingNode>(new PrefixRule(type + " ", discard), ";",
                                                                                 ResultFactoryImpl.get())),
                              JavaLang.FACTORY);
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createTypeRule() {
        return new OrRule<EverythingNode>(List.of(JavaLang.createGenericRule(),
                                                  new TypeRule<>("identifier", new StringRule("value"), JavaLang.FACTORY)),
                                          ResultFactoryImpl.get());
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createGenericRule() {
        return new TypeRule<>("generic", new StripRule(
                new SuffixRule<EverythingNode>(SplitRule.First(new StringRule("base"), "<", new StringRule("value")), ">",
                                               ResultFactoryImpl.get())),
                              JavaLang.FACTORY);
    }
}
