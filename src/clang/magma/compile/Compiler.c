package magma.compile;class package magma.compile;{package magma.compile;

import jvm.collect.list.Lists;class import jvm.collect.list.Lists;{

import jvm.collect.list.Lists;
import magma.collect.list.List_;class import magma.collect.list.List_;{
import magma.collect.list.List_;
import magma.compile.lang.CLang;class import magma.compile.lang.CLang;{
import magma.compile.lang.CLang;
import magma.compile.lang.JavaCTransformer;class import magma.compile.lang.JavaCTransformer;{
import magma.compile.lang.JavaCTransformer;
import magma.compile.lang.JavaLang;class import magma.compile.lang.JavaLang;{
import magma.compile.lang.JavaLang;
import magma.result.Result;class import magma.result.Result;{
import magma.result.Result;

public class Compiler {
    public static Result<String, CompileError> compile(String input, List_<String> namespace, String name) {
        return JavaLang.createJavaRootRule().parse(input)
                .mapValue(tree -> new JavaCTransformer().transform(tree, namespace))
                .flatMapValue(generate -> CLang.createCRootRule().generate(generate))
                .mapValue(output -> complete(namespace, name, output));
    }

    private static String complete(List_<String> namespace, String name, String output) {
        if (namespace.equals(Lists.of("magma")) && name.equals("Main")) {
            return output + "int main(){\n\treturn 0;\n}\n";
        }

        return output;
    }
}class public class Compiler {
    public static Result<String, CompileError> compile(String input, List_<String> namespace, String name) {
        return JavaLang.createJavaRootRule().parse(input)
                .mapValue(tree -> new JavaCTransformer().transform(tree, namespace))
                .flatMapValue(generate -> CLang.createCRootRule().generate(generate))
                .mapValue(output -> complete(namespace, name, output));
    }

    private static String complete(List_<String> namespace, String name, String output) {
        if (namespace.equals(Lists.of("magma")) && name.equals("Main")) {
            return output + "int main(){\n\treturn 0;\n}\n";
        }

        return output;
    }
}{

public class Compiler {
    public static Result<String, CompileError> compile(String input, List_<String> namespace, String name) {
        return JavaLang.createJavaRootRule().parse(input)
                .mapValue(tree -> new JavaCTransformer().transform(tree, namespace))
                .flatMapValue(generate -> CLang.createCRootRule().generate(generate))
                .mapValue(output -> complete(namespace, name, output));
    }

    private static String complete(List_<String> namespace, String name, String output) {
        if (namespace.equals(Lists.of("magma")) && name.equals("Main")) {
            return output + "int main(){\n\treturn 0;\n}\n";
        }

        return output;
    }
}