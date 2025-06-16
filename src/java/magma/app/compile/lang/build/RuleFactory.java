package magma.app.compile.lang.build;

import java.util.List;

public interface RuleFactory<Rule> {
    Rule String(String value);

    Rule Strip(Rule rule);

    Rule Last(Rule parent, String infix, Rule child);

    Rule Suffix(Rule last, String suffix);

    Rule Prefix(Rule suffix);

    Rule NodeList(List<Rule> children);

    Rule Empty();
}
