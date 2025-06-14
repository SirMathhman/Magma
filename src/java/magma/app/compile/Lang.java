package magma.app.compile;

import magma.app.compile.node.CompoundNode;
import magma.app.compile.rule.DivideRule;
import magma.app.compile.rule.InfixRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.factory.PropertiesCompoundNodeFactory;

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