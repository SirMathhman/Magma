package magma.app.compile.lang.common;

import magma.app.compile.rule.NodeListRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;
import magma.app.compile.rule.locate.First;
import magma.app.compile.rule.locate.LocateRule;
import magma.app.compile.split.ParamSplitter;

public class Generics {
    public static TypeRule createGenericRule(Rule type) {
        var genericType = new StripRule(new StringRule("generic-type"));
        var arguments = new NodeListRule(new ParamSplitter(), "type-arguments", type);
        return new TypeRule("generic", new LocateRule(genericType, new First("<"), new StripRule(new SuffixRule(arguments, ">"))));
    }
}