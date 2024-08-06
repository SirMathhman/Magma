package magma.app.compile.lang.common;

import magma.app.compile.rule.Last;
import magma.app.compile.rule.LocateRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

public class Declarations {
    public static final String DEFINITION = "definition";
    public static final String DECLARATION = "declaration";
    public static final String VALUE = "value";
    public static final String BEFORE_DEFINITION = "before-definition";
    public static final String AFTER_DEFINITION = "after-definition";
    public static final String AFTER_VALUE = "after-value";
    public static final String BEFORE_VALUE = "before-value";

    public static TypeRule createDeclarationRule(Rule definition, Rule valueRule) {
        var definitionProperty = new StripRule(new NodeRule(DEFINITION, definition), BEFORE_DEFINITION, AFTER_DEFINITION);
        var valueProperty = new StripRule(new NodeRule(VALUE, valueRule), BEFORE_VALUE, AFTER_VALUE);
        return new TypeRule(DECLARATION, new LocateRule(definitionProperty, new Last("="), new SuffixRule(valueProperty, ";")));
    }
}