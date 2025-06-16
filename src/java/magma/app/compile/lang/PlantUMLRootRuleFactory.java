package magma.app.compile.lang;

import magma.app.compile.lang.build.RuleFactory;

import java.util.List;

public record PlantUMLRootRuleFactory<Rule>(RuleFactory<Rule> builder) {
    public Rule create() {
        return this.builder.NodeList(List.of(this.createDependencyRule(), this.builder.Empty()));
    }

    private Rule createDependencyRule() {
        final var parent = this.builder.String("parent");
        final var child = this.builder.String("child");
        return this.builder.Suffix(this.builder.Last(parent, " --> ", child), "\n");
    }
}