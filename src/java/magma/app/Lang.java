package magma.app;

import magma.app.node.CompoundNode;
import magma.app.rule.DivideRule;
import magma.app.rule.InfixRule;
import magma.app.rule.PrefixRule;
import magma.app.rule.Rule;
import magma.app.rule.StringRule;
import magma.app.rule.StripRule;
import magma.app.rule.SuffixRule;
import magma.app.rule.factory.PropertiesCompoundNodeFactory;

public class Lang {
    public static Rule<CompoundNode> createJavaRootRule() {
        return new DivideRule<>("children", createImportRule(), new PropertiesCompoundNodeFactory());
    }

    public static Rule<CompoundNode> createPlantRootRule() {
        return new DivideRule<>("children", createDependencyRule(), new PropertiesCompoundNodeFactory());
    }

    static Rule<CompoundNode> createImportRule() {
        final var parent = new StringRule<>("parent", new PropertiesCompoundNodeFactory());
        final var destination = new StringRule<>("destination", new PropertiesCompoundNodeFactory());
        final var rule = new PrefixRule<>("import ", new SuffixRule<>(new InfixRule<>(parent, ".", destination), ";"));
        return new StripRule<>(rule);
    }

    static Rule<CompoundNode> createDependencyRule() {
        return new SuffixRule<>(new InfixRule<>(new StringRule<>("source", new PropertiesCompoundNodeFactory()), " --> ", new StringRule<>("destination", new PropertiesCompoundNodeFactory())), "\n");
    }
}