package magma.app.compile.value;

import magma.api.collect.Joiner;
import magma.api.collect.list.Iterable;
import magma.app.DefiningCompiler;
import magma.app.TypeCompiler;
import magma.app.compile.CompileState;
import magma.app.compile.define.Definition;
import magma.app.compile.node.Node;

public record Lambda(Iterable<Definition> paramNames, String content) implements Node {
    public String generate() {
        var joinedParamNames = this.paramNames.iter()
                .map((Definition definition) -> {
                    return DefiningCompiler.generateParameter(definition);
                })
                .collect(new Joiner(", "))
                .orElse("");

        return "(" + joinedParamNames + ")" + " => " + this.content;
    }

    public Node resolve(CompileState state) {
        return TypeCompiler.Unknown;
    }


    public boolean is(String type) {
        return false;
    }
}
