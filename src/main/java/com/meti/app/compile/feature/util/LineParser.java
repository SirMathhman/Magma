package com.meti.app.compile.feature.util;

import com.meti.api.option.Option;
import com.meti.api.option.Some;
import com.meti.app.compile.node.Node;
import com.meti.app.compile.parse.AbstractParser;
import com.meti.app.compile.parse.State;
import com.meti.app.compile.stage.CompileException;

public class LineParser extends AbstractParser {
    public LineParser(State state) {
        super(state);
    }

    @Override
    protected boolean isValid() {
        return state.applyToCurrent(current -> current.is(Node.Category.Line));
    }

    @Override
    protected State onEnterImpl() {
        return state.enter();
    }

    @Override
    protected State onExitImpl() {
        return state.exit();
    }

    @Override
    protected Option<State> modifyAfterASTImpl() {
        return new Some<>(modifyAfterASTImpl2());
    }

    private State modifyAfterASTImpl2() {
        return state;
    }

    @Override
    protected Option<State> modifyBeforeASTImpl() throws CompileException {
        return new Some<>(modifyBeforeASTImpl2());
    }

    private State modifyBeforeASTImpl2() throws CompileException {
        return state;
    }
}
