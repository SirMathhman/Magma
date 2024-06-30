package magma.build.compile.lang;

import magma.build.compile.CompileError;
import magma.build.compile.rule.split.BackwardsRule;
import magma.build.compile.rule.split.LastRule;
import magma.build.compile.rule.split.Splitter;
import magma.build.compile.rule.text.extract.ExtractNodeRule;
import magma.build.compile.rule.text.extract.ExtractStringListRule;
import magma.build.compile.rule.text.extract.ExtractStringRule;
import magma.build.compile.Error_;
import magma.build.compile.rule.ContextRule;
import magma.build.compile.rule.OrRule;
import magma.build.compile.rule.Rule;
import magma.build.compile.rule.TypeRule;
import magma.build.compile.rule.split.SplitMultipleRule;
import magma.build.compile.rule.text.LeftRule;
import magma.build.compile.rule.text.RightRule;
import magma.build.compile.rule.text.StripRule;

import java.util.List;
import java.util.Optional;

public class JavaDefinitionHeaderFactory {
    static Rule createDefinitionHeaderRule() {
        var type = new ExtractNodeRule("type", Lang.createTypeRule());
        var name = new ExtractStringRule("name");

        var generics = new LeftRule("<", new RightRule(Lang.createTypeParamsRule(), ">"));
        var withGenerics = new ContextRule("With generics.", new StripRule(new BackwardsRule(generics, " ", type)));
        var withoutGenerics = new ContextRule("Without generics.", type);
        var maybeGenerics = new OrRule(List.of(withGenerics, withoutGenerics));

        var modifiers = new ModifiersRule();
        var withModifiers = new ContextRule("With modifiers.", new BackwardsRule(modifiers, " ", maybeGenerics));
        var withoutModifiers = new ContextRule("Without modifiers.", maybeGenerics);
        var maybeModifiers = new OrRule(List.of(withModifiers, withoutModifiers));

        var annotation = new TypeRule("annotation", new LeftRule("@", new ExtractStringRule("value")));
        var annotations = new SplitMultipleRule(new SimpleSplitter(), ", ", "annotations", annotation);
        var withAnnotations = new ContextRule("With annotations.",new LastRule(annotations, "\r\n", maybeModifiers));
        var withoutAnnotations = new ContextRule("Without annotations.", maybeModifiers);
        var maybeAnnotations = new OrRule(List.of(withAnnotations, withoutAnnotations));

        var beforeName = new ContextRule("Cannot parse before name.", maybeAnnotations);
        return new TypeRule("definition", new StripRule(new LastRule(beforeName, " ", name)));
    }

    private static class ModifiersRule extends ExtractStringListRule {
        public static final List<String> MODIFIERS = List.of(
                "public",
                "static",
                "final",
                "private",
                "default",
                "protected",
                "abstract"
        );

        public ModifiersRule() {
            super("modifiers", " ");
        }

        @Override
        protected Optional<Error_> qualify(String child) {
            if (MODIFIERS.contains(child)) {
                return Optional.empty();
            } else {
                return Optional.of(new CompileError("Invalid modifier.", child));
            }
        }
    }

    private static class SimpleSplitter implements Splitter {
        @Override
        public List<String> split(String input) {
            return List.of(input.split("\r\n"));
        }
    }
}
