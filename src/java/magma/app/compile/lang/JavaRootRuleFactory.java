package magma.app.compile.lang;

import magma.app.compile.lang.build.RuleBuilder;

import java.util.List;

public final class JavaRootRuleFactory<Rule> {
    private final RuleBuilder<Rule> builder;

    public JavaRootRuleFactory(RuleBuilder<Rule> builder) {
        this.builder = builder;
    }

    public Rule create() {
        final var strip = this.createImportRule();
        return this.builder.NodeList(List.of(strip, this.builder.String("value")));
    }

    private Rule createImportRule() {
        final var parent = this.builder.String("parent");
        final var child = this.builder.String("child");
        return this.builder.Strip(this.builder.Prefix(this.builder.Suffix(this.builder.Last(parent, ".", child), ";")));
    }
}