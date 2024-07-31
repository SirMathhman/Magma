package magma;

import java.util.ArrayList;
import java.util.List;

public class Compiler {
    private static Result<String, CompileException> generate(List<Node> children) {
        var rootMagmaRule = MagmaLang.createMagmaRootMemberRule();
        Result<StringBuilder, CompileException> builder = new Ok<>(new StringBuilder());
        for (Node child : children) {
            builder = builder
                    .and(() -> rootMagmaRule.generate(child).mapErr(err -> err))
                    .mapValue(tuple -> tuple.left().append(tuple.right()));
        }

        return builder.mapValue(StringBuilder::toString);
    }

    private static Result<List<Node>, CompileException> parse(List<String> rootMembers) {
        var javaRootMember = JavaLang.createJavaRootMemberRule();
        Result<List<Node>, CompileException> childrenResult = new Ok<>(new ArrayList<>());
        for (var rootMember : rootMembers) {
            var stripped = rootMember.strip();
            if(stripped.isEmpty()) continue;

            childrenResult = childrenResult
                    .and(() -> javaRootMember.parse(stripped).mapErr(err -> err))
                    .mapValue(Compiler::add);
        }

        return childrenResult;
    }

    private static ArrayList<Node> add(Tuple<List<Node>, Node> tuple) {
        var copy = new ArrayList<>(tuple.left());
        copy.add(tuple.right());
        return copy;
    }

    private static ArrayList<Node> modify(List<Node> list) {
        var copy = new ArrayList<Node>();
        for (Node node : list) {
            if (node.is(JavaLang.PACKAGE)) continue;

            if (node.is(JavaLang.CLASS)) {
                copy.add(node.retype(MagmaLang.FUNCTION).mapString(CommonLang.MODIFIERS, oldModifiers -> {
                    var newAccessor = oldModifiers.equals(JavaLang.PUBLIC_KEYWORD_WITH_SPACE) ? MagmaLang.EXPORT_KEYWORD_WITH_SPACE : "";
                    return newAccessor + CommonLang.CLASS_KEYWORD_WITH_SPACE;
                }));
            } else {
                copy.add(node);
            }
        }
        return copy;
    }

    String compile(String input) throws CompileException {
        var rootMembers = Splitter.splitRootMembers(input);

        return parse(rootMembers)
                .mapValue(Compiler::modify)
                .flatMapValue(Compiler::generate)
                .$();
    }
}