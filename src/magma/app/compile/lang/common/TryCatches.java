package magma.app.compile.lang.common;

import magma.app.compile.split.ParamSplitter;
import magma.app.compile.rule.LazyRule;
import magma.app.compile.rule.NodeListRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.TypeRule;
import magma.app.compile.rule.locate.First;
import magma.app.compile.rule.locate.LocateRule;

public class TryCatches {
    public static TypeRule createCatchRule(Rule definition, LazyRule statement) {
        return PrefixedStatements.createPrefixedStatementRule("catch", "catch", statement, children -> captureCatchParameters(children, definition));
    }

    private static Rule captureCatchParameters(Rule children, Rule definition) {
        var params = new NodeListRule(new ParamSplitter(), "params", definition);
        return new StripRule(new PrefixRule("(", new LocateRule(params, new First(")"), children)));
    }
}
