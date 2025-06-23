package magma.app.compile.rule;

public interface NodeListResult<Result> {
    NodeListResult<Result> add(Result result);

    Result toNode(String key);
}
