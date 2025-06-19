package magma.app.compile.rule.action;

import magma.api.collect.list.ListLike;
import magma.api.collect.list.Lists;

public record CompileError(String message, String context, ListLike<CompileError> errors) {
    public CompileError(String message, String context) {
        this(message, context, Lists.empty());
    }
}
