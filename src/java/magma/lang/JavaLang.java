package magma.lang;

import magma.compile.result.ResultFactory;
import magma.compile.result.ResultFactoryImpl;
import magma.error.FormatError;
import magma.node.EverythingNode;
import magma.node.factory.MapNodeFactory;
import magma.node.result.NodeResult;
import magma.rule.IdentifierRule;
import magma.rule.OrRule;
import magma.rule.PrefixRule;
import magma.rule.Rule;
import magma.rule.StringRule;
import magma.rule.StripRule;
import magma.rule.SuffixRule;
import magma.rule.TypeRule;
import magma.string.result.StringResult;

import java.util.List;

public class JavaLang {
    private static final ResultFactory<EverythingNode, FormatError, StringResult<FormatError>, NodeResult<EverythingNode, FormatError, StringResult<FormatError>>> FACTORY = ResultFactoryImpl.get();

    private JavaLang() {}

    public static Rule<EverythingNode, NodeResult<EverythingNode, FormatError, StringResult<FormatError>>, StringResult<FormatError>> createJavaRootSegmentRule() {
        return new OrRule<>(List.of(JavaLang.createNamespaceRule("package"), JavaLang.createNamespaceRule("import"),
                                    JavaLang.createStructureRule("class"), JavaLang.createStructureRule("interface"),
                                    JavaLang.createStructureRule("record")), ResultFactoryImpl.get());
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode, FormatError, StringResult<FormatError>>, StringResult<FormatError>> createStructureRule(final String type) {
        final var anImplements = JavaLang.createStructureHeaderRule(type);

        final Rule<EverythingNode, NodeResult<EverythingNode, FormatError, StringResult<FormatError>>, StringResult<FormatError>> content =
                new StringRule<>("content", ResultFactoryImpl.get(), new MapNodeFactory());
        return new TypeRule<>(type, new StripRule<>(
                new SuffixRule<>(CommonLang.First(anImplements, "{", content), "}", ResultFactoryImpl.get())),
                              JavaLang.FACTORY);
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode, FormatError, StringResult<FormatError>>, StringResult<FormatError>> createStructureHeaderRule(
            final String type) {
        final Rule<EverythingNode, NodeResult<EverythingNode, FormatError, StringResult<FormatError>>, StringResult<FormatError>> name = new StripRule<>(
                new IdentifierRule<>(new StringRule<>("name", ResultFactoryImpl.get(), new MapNodeFactory()),
                                     ResultFactoryImpl.get()));

        final var withTypeParameters = new SuffixRule<>(CommonLang.First(name, "<", new StringRule<>("type-parameters",
                                                                                                     ResultFactoryImpl.get(),
                                                                                                     new MapNodeFactory())),
                                                        ">", ResultFactoryImpl.get());
        final Rule<EverythingNode, NodeResult<EverythingNode, FormatError, StringResult<FormatError>>, StringResult<FormatError>>
                maybeWithTypeParams =
                new OrRule<>(List.of(new StripRule<>(withTypeParameters), name), ResultFactoryImpl.get());

        final Rule<EverythingNode, NodeResult<EverythingNode, FormatError, StringResult<FormatError>>, StringResult<FormatError>>
                params = new StringRule<>("params", ResultFactoryImpl.get(), new MapNodeFactory());
        final var withParameters =
                CommonLang.First(CommonLang.First(maybeWithTypeParams, "(", params), ")",
                                 new StringRule<>("more", ResultFactoryImpl.get(), new MapNodeFactory()));
        final Rule<EverythingNode, NodeResult<EverythingNode, FormatError, StringResult<FormatError>>, StringResult<FormatError>>
                maybeWithParameters = new OrRule<>(List.of(withParameters, maybeWithTypeParams), ResultFactoryImpl.get());

        final var withSuperClass = CommonLang.Last(maybeWithParameters, "extends ", JavaLang.createTypeRule());
        final Rule<EverythingNode, NodeResult<EverythingNode, FormatError, StringResult<FormatError>>, StringResult<FormatError>>
                maybeWithSuperClass = new OrRule<>(List.of(withSuperClass, maybeWithParameters), ResultFactoryImpl.get());

        final var withImplementing = CommonLang.Last(maybeWithSuperClass, "implements", JavaLang.createTypeRule());
        final Rule<EverythingNode, NodeResult<EverythingNode, FormatError, StringResult<FormatError>>, StringResult<FormatError>>
                maybeWithImplements = new OrRule<>(List.of(withImplementing, maybeWithSuperClass), ResultFactoryImpl.get());

        final Rule<EverythingNode, NodeResult<EverythingNode, FormatError, StringResult<FormatError>>, StringResult<FormatError>> modifiers =
                new StringRule<>("modifiers", ResultFactoryImpl.get(), new MapNodeFactory());
        return CommonLang.First(modifiers, type + " ", maybeWithImplements);
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode, FormatError, StringResult<FormatError>>, StringResult<FormatError>> createNamespaceRule(final String type) {
        final Rule<EverythingNode, NodeResult<EverythingNode, FormatError, StringResult<FormatError>>, StringResult<FormatError>> child =
                new StringRule<>("child", ResultFactoryImpl.get(), new MapNodeFactory());

        final var discard = new OrRule<>(
                List.of(CommonLang.Last(new StringRule<>("discard", ResultFactoryImpl.get(), new MapNodeFactory()), ".",
                                        child), child), ResultFactoryImpl.get());
        return new TypeRule<>(type, new StripRule<>(
                new SuffixRule<>(new PrefixRule<>(type + " ", discard, ResultFactoryImpl.get()), ";", ResultFactoryImpl.get())),
                              JavaLang.FACTORY);
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode, FormatError, StringResult<FormatError>>, StringResult<FormatError>> createTypeRule() {
        return new OrRule<>(List.of(JavaLang.createGenericRule(),
                                    new TypeRule<>("identifier", new StringRule<>("value", ResultFactoryImpl.get(), new MapNodeFactory()),
                                                   JavaLang.FACTORY)), ResultFactoryImpl.get());
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode, FormatError, StringResult<FormatError>>, StringResult<FormatError>> createGenericRule() {
        return new TypeRule<>("generic", new StripRule<>(new SuffixRule<>(
                CommonLang.First(new StringRule<>("base", ResultFactoryImpl.get(), new MapNodeFactory()), "<",
                                 new StringRule<>("value", ResultFactoryImpl.get(), new MapNodeFactory())), ">",
                ResultFactoryImpl.get())),
                              JavaLang.FACTORY);
    }
}
