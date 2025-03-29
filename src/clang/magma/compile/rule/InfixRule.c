package magma.compile.rule;class package magma.compile.rule;{package magma.compile.rule;

import magma.compile.CompileError;class import magma.compile.CompileError;{

import magma.compile.CompileError;
import magma.compile.Node;class import magma.compile.Node;{
import magma.compile.Node;
import magma.result.Result;class import magma.result.Result;{
import magma.result.Result;

public record InfixRule(Rule left, String infix, Rule right) implements  Rule {
    @Override
    public Result<Node, CompileError> parse(String input) {
        return left.parse(input).and(() -> right.parse(input)).mapValue(tuple -> tuple.left().merge(tuple.right()));
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        return left.generate(node).and(() -> right.generate(node)).mapValue(tuple -> tuple.left() + infix + tuple.right());
    }
}
class public record InfixRule(Rule left, String infix, Rule right) implements  Rule {
    @Override
    public Result<Node, CompileError> parse(String input) {
        return left.parse(input).and(() -> right.parse(input)).mapValue(tuple -> tuple.left().merge(tuple.right()));
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        return left.generate(node).and(() -> right.generate(node)).mapValue(tuple -> tuple.left() + infix + tuple.right());
    }
}{

public record InfixRule(Rule left, String infix, Rule right) implements  Rule {
    @Override
    public Result<Node, CompileError> parse(String input) {
        return left.parse(input).and(() -> right.parse(input)).mapValue(tuple -> tuple.left().merge(tuple.right()));
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        return left.generate(node).and(() -> right.generate(node)).mapValue(tuple -> tuple.left() + infix + tuple.right());
    }
}
