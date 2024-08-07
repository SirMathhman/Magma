package magma.app.compile.lang.common;

import magma.app.compile.lang.java.JavaLang;
import magma.app.compile.rule.LazyRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;
import magma.app.compile.rule.locate.First;
import magma.app.compile.rule.locate.LocateRule;

public class Structs {
    public static Rule createStructRule(String type, String slice, Rule member) {
        var classRule = new LazyRule();
        var modifiers = Modifiers.createModifiersRule();

        var content = new NodeRule("value", Blocks.createBlockRule(member));
        var after = new LocateRule(new StripRule(new StringRule(JavaLang.CLASS_NAME)), new First("{"), new SuffixRule(content, "}"));
        classRule.set(new TypeRule(type, new LocateRule(modifiers, new First(slice), after)));
        return classRule;
    }
}
