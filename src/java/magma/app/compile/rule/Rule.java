package magma.app.compile.rule;

import magma.app.compile.rule.action.Generator;
import magma.app.compile.rule.action.Lexer;

public interface Rule<Node, NodeResult, StringResult> extends Lexer<NodeResult>, Generator<Node, StringResult> {
}