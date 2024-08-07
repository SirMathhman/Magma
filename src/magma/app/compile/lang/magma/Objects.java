package magma.app.compile.lang.magma;

import magma.app.compile.lang.common.CommonLang;
import magma.app.compile.lang.common.Blocks;
import magma.app.compile.rule.DisjunctionRule;
import magma.app.compile.rule.locate.First;
import magma.app.compile.rule.locate.Last;
import magma.app.compile.rule.locate.LocateRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

import java.util.List;

public class Objects {
    public static final String OBJECT = "object";
    public static final String VALUE = "value";
    public static final String NAME = "name";

    public static TypeRule createObjectRule(Rule statement) {
        var modifiers = CommonLang.createModifiersRule();
        var name = new StringRule(NAME);
        var value = new NodeRule(VALUE, Blocks.createBlockRule(statement));

        var afterKeyword = new LocateRule(name, new First("{"), new SuffixRule(value, "}"));
        return new TypeRule(OBJECT, new DisjunctionRule(List.of(
                new LocateRule(modifiers, new Last("object "), afterKeyword),
                new PrefixRule("object ", afterKeyword)
        )));
    }
}
