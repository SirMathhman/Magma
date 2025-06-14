package magma.app;

import magma.app.node.CompoundNode;
import magma.app.rule.DivideRule;
import magma.app.rule.InfixRule;
import magma.app.rule.PrefixRule;
import magma.app.rule.factory.PropertiesCompoundNodeFactory;
import magma.app.rule.Rule;
import magma.app.rule.StringRule;
import magma.app.rule.StripRule;
import magma.app.rule.SuffixRule;

public class Lang {
    public static Rule<CompoundNode> createJavaRootRule() {
        return new DivideRule<CompoundNode>("children", createImportRule(), new PropertiesCompoundNodeFactory());
    }

    public static Rule<CompoundNode> createPlantRootRule() {
        return new DivideRule<CompoundNode>("children", createDependencyRule(), new PropertiesCompoundNodeFactory());
    }

    static Rule<CompoundNode> createImportRule() {
        final var parent = new StringRule<CompoundNode>("parent", new PropertiesCompoundNodeFactory());
        final var destination = new StringRule<CompoundNode>("destination", new PropertiesCompoundNodeFactory());
        final var rule = new PrefixRule<CompoundNode>("import ", new SuffixRule<CompoundNode>(new InfixRule<CompoundNode>(parent, ".", destination), ";"));
        return new StripRule<CompoundNode>(rule);
    }

    static Rule<CompoundNode> createDependencyRule() {
        return new SuffixRule<CompoundNode>(new InfixRule<CompoundNode>(new StringRule<CompoundNode>("source", new PropertiesCompoundNodeFactory()), " --> ", new StringRule<CompoundNode>("destination", new PropertiesCompoundNodeFactory())), "\n");
    }
}