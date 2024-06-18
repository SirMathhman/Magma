package magma.compile.lang;

import magma.api.Tuple;
import magma.compile.rule.Node;

import java.util.List;
import java.util.Optional;

public class JavaToMagmaGenerator extends Generator {
    private static Optional<Node> removePackagesFromBlock(Node node) {
        if (!node.is("block")) return Optional.empty();

        return Optional.of(node.mapNodes("children", JavaToMagmaGenerator::removePackagesFromList));
    }

    private static List<Node> removePackagesFromList(List<Node> children) {
        return children.stream()
                .filter(child -> !child.is("package"))
                .toList();
    }

    @Override
    protected Tuple<Node, Integer> preVisit(Node node, int depth) {
        var newNode = removePackagesFromBlock(node)
                .or(() -> replaceClassWithFunction(node))
                .or(() -> replaceMethodWithFunction(node))
                .or(() -> replaceLambdaWithFunction(node))
                .or(() -> replaceRecordWithFunction(node))
                .or(() -> replaceConstructorsWithInvocation(node))
                .or(() -> replaceInterfaceWithStruct(node))
                .or(() -> replaceMethodReferenceWithAccess(node))
                .orElse(node);

        return new Tuple<>(newNode, depth);
    }

    private Optional<Node> replaceMethodReferenceWithAccess(Node node) {
        if (!node.is("method-reference")) return Optional.empty();

        return Optional.of(node.retype("access"));
    }

    private Optional<Node> replaceRecordWithFunction(Node node) {
        if (!node.is("record")) return Optional.empty();

        return Optional.of(node.retype("function"));
    }

    private Optional<Node> replaceInterfaceWithStruct(Node node) {
        if (!node.is("interface")) return Optional.empty();

        return Optional.of(node.retype("struct"));
    }

    private Optional<Node> replaceLambdaWithFunction(Node node) {
        if (!node.is("lambda")) return Optional.empty();

        return Optional.of(node.retype("function"));
    }

    private Optional<Node> replaceConstructorsWithInvocation(Node node) {
        if (!node.is("constructor")) return Optional.empty();

        return Optional.of(node.retype("invocation"));
    }

    private Optional<Node> replaceMethodWithFunction(Node node) {
        if (!node.is("method")) return Optional.empty();

        return Optional.of(node.retype("function"));
    }

    private Optional<Node> replaceClassWithFunction(Node node) {
        if (!node.is("class")) return Optional.empty();

        return Optional.of(node.retype("function"));
    }
}
