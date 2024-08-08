package magma.app.compile.lang.common;

import magma.app.compile.rule.OptionalRule;
import magma.app.compile.split.MemberSplitter;
import magma.app.compile.rule.DisjunctionRule;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.NodeListRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.TypeRule;

import java.util.List;

public class Blocks {
    public static final String CHILDREN = "children";
    public static final String BEFORE_CHILD = "before-child";
    public static final String AFTER_CHILD = "after-child";
    public static final String BLOCK = "block";
    public static final String BEFORE_CHILDREN = "before-children";
    public static final String AFTER_CHILDREN = "after-children";

    public static Rule createMembersRule(Rule childRule) {
        var wrappedChild = new StripRule(childRule, BEFORE_CHILD, AFTER_CHILD);
        var property = new NodeListRule(new MemberSplitter(), CHILDREN, wrappedChild);

        var onChildrenPresent = new StripRule(property, BEFORE_CHILDREN, AFTER_CHILDREN);
        var onChildrenEmpty = new StripRule(EmptyRule.EMPTY_RULE);
        return new OptionalRule(CHILDREN, onChildrenPresent, onChildrenEmpty);
    }

    public static Rule createBlockRule(Rule childRule) {
        return new TypeRule(BLOCK, createMembersRule(new DisjunctionRule(List.of(
                childRule
        ))));
    }
}
