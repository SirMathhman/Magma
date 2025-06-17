package magma.app.compile.rule.or;

import magma.api.result.Result;
import magma.app.compile.error.FormattedError;
import magma.app.compile.result.ResultCreator;
import magma.app.compile.result.SimpleResultCreator;

public class ResultFactoryImpl<Node> implements ResultFactory<Node, Result<Node, FormattedError>, Result<String, FormattedError>> {
    public ResultFactoryImpl() {
    }

    @Override
    public ResultCreator<Node, Result<Node, FormattedError>> createNodeCreator() {
        return new SimpleResultCreator<>();
    }

    @Override
    public ResultCreator<String, Result<String, FormattedError>> createStringCreator() {
        return new SimpleResultCreator<>();
    }
}