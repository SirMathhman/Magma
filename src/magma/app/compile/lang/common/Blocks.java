package magma.app.compile.lang.common;

import magma.app.compile.MemberSplitter;
import magma.app.compile.rule.NodeListRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.TypeRule;

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
        return new StripRule(property, BEFORE_CHILDREN, AFTER_CHILDREN);
    }

    public static Rule createBlockRule(Rule childRule) {
        return new TypeRule(BLOCK, createMembersRule(childRule));
    }
}