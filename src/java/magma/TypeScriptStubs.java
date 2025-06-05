package magma;

import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.JavaFile;
import magma.result.Results;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utility for generating TypeScript stub files mirroring the Java sources.
 * Instances contain no state and all methods are static.
 */
public final class TypeScriptStubs {
    private TypeScriptStubs() {
    }

    public static Option<IOException> write(PathLike javaRoot, PathLike tsRoot) {
        return javaRoot.walk().match(stream -> {
            List<PathLike> files = stream.filter(PathLike::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .toList();

            for (PathLike file : files) {
                Option<IOException> res = processFile(javaRoot, tsRoot, file);
                if (res.isPresent()) {
                    return res;
                }
            }
            return new None<>();
        }, Some::new);
    }

    private static Option<IOException> processFile(PathLike javaRoot, PathLike tsRoot,
                                                   PathLike file) {
        PathLike relative = javaRoot.relativize(file);
        PathLike tsFile = tsRoot.resolve(relative.toString().replaceFirst("\\.java$", ".ts"));
        var dirResult = tsFile.getParent().createDirectories();
        if (dirResult.isPresent()) {
            return dirResult;
        }

        JavaFile jf = new JavaFile(file);

        var importsRes = jf.imports();
        if (importsRes.isErr()) {
            return new Some<>(((Err<List<String>, IOException>) importsRes).error());
        }

        var pkgRes = jf.packageName();
        if (pkgRes.isErr()) {
            return new Some<>(((Err<String, IOException>) pkgRes).error());
        }

        var localRes = jf.localDependencies();
        if (localRes.isErr()) {
            return new Some<>(((Err<List<String>, IOException>) localRes).error());
        }

        var declarationsRes = jf.declarations();
        if (declarationsRes.isErr()) {
            return new Some<>(((Err<List<String>, IOException>) declarationsRes).error());
        }

        var methodsRes = jf.methods();
        if (methodsRes.isErr()) {
            return new Some<>(((Err<Map<String, List<String>>, IOException>) methodsRes).error());
        }

        List<String> imports = Results.unwrap(importsRes);
        String pkgName = Results.unwrap(pkgRes);
        List<String> locals = Results.unwrap(localRes);
        mergeLocalImports(imports, locals, pkgName);

        List<String> declarations = Results.unwrap(declarationsRes);
        Map<String, List<String>> methods = Results.unwrap(methodsRes);

        String content = stubContent(relative, tsFile.getParent(), tsRoot,
                imports, declarations, methods);
        var writeRes = tsFile.writeString(content);
        if (writeRes.isPresent()) {
            return writeRes;
        }
        return new None<>();
    }

    private static void mergeLocalImports(List<String> imports, List<String> locals,
                                          String pkgName) {
        for (String dep : locals) {
            String fqn = pkgName.isEmpty() ? dep : pkgName + "." + dep;
            if (!imports.contains(fqn)) {
                imports.add(fqn);
            }
        }
    }

    private static String stubContent(PathLike relative, PathLike from, PathLike root,
                                      List<String> imports,
                                      List<String> declarations,
                                      Map<String, List<String>> methods) {
        StringBuilder builder = new StringBuilder();
        builder.append("// Auto-generated from ").append(relative).append(System.lineSeparator());

        Map<String, List<String>> byPath = groupImports(imports, from, root);
        appendImportLines(builder, byPath);

        if (declarations.isEmpty()) {
            builder.append("export {};").append(System.lineSeparator());
            return builder.toString();
        }

        appendDeclarations(builder, relative, declarations, methods);
        return builder.toString();
    }

    private static Map<String, List<String>> groupImports(List<String> imports, PathLike from, PathLike root) {
        Map<String, List<String>> byPath = new LinkedHashMap<>();
        for (String imp : imports) {
            String className = imp.substring(imp.lastIndexOf('.') + 1);
            PathLike target = root.resolve(imp.replace('.', '/') + ".ts");
            PathLike rel = from.relativize(target);
            String path = rel.toString().replace('\\', '/');
            path = path.replaceFirst("\\.ts$", "");
            if (!path.startsWith(".")) {
                path = "./" + path;
            }
            byPath.computeIfAbsent(path, _ -> new ArrayList<>()).add(className);
        }
        return byPath;
    }

    private static void appendImportLines(StringBuilder builder, Map<String, List<String>> byPath) {
        for (var entry : byPath.entrySet()) {
            builder.append("import { ");
            builder.append(String.join(", ", entry.getValue()));
            builder.append(" } from \"").append(entry.getKey()).append("\"");
            builder.append(";").append(System.lineSeparator());
        }
    }

    private static void appendDeclarations(StringBuilder builder, PathLike relative,
                                           List<String> declarations,
                                           Map<String, List<String>> methods) {
        var namePattern = Pattern.compile("export \\w+ (\\w+)(?:<[^>]+>)?");
        boolean isMain = relative.toString().replace('\\', '/').equals("magma/Main.java");
        for (String decl : declarations) {
            appendDeclaration(builder, decl, namePattern, methods);
        }
        if (isMain) {
            builder.append("Main.main([]);").append(System.lineSeparator());
        }
    }

    private static void appendDeclaration(StringBuilder builder, String decl,
                                          Pattern namePattern,
                                          Map<String, List<String>> methods) {
        var m = namePattern.matcher(decl);
        if (!m.find()) {
            builder.append(decl).append(System.lineSeparator());
            return;
        }
        String name = m.group(1);
        List<String> mList = methods.getOrDefault(name, Collections.emptyList());
        if (mList.isEmpty()) {
            builder.append(decl).append(System.lineSeparator());
            return;
        }
        builder.append(decl, 0, decl.length() - 1).append(System.lineSeparator());
        for (String method : mList) {
            builder.append(method).append(System.lineSeparator());
        }
        builder.append("}").append(System.lineSeparator());
    }

}
