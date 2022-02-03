package com.meti.app.compile.parse;

import com.meti.app.compile.node.Node;
import com.meti.app.compile.node.attribute.Attribute;
import com.meti.app.compile.stage.CompileException;

public class VariableVisitor extends AbstractParser {
    protected VariableVisitor(State state) {
        super(state);
    }

    @Override
    protected boolean isValid() {
        return state.queryCurrent(value -> value.is(Node.Category.Variable));
    }

    @Override
    protected State modifyBeforeASTImpl() throws CompileException {
        var value = state.getCurrent().apply(Attribute.Category.Value).asInput();
        var format = value.toOutput().compute();
        if (!state.getScope().isDefined(format)) {
            var message = "'%s' is not defined.";
            var scope = message.formatted(format);
            throw new CompileException(scope);
        }

        return state;
    }
}
