package magma.app.compile.type.resolve.template;

import jvm.api.collect.list.Lists;
import magma.api.Tuple2;
import magma.api.Tuple2Impl;
import magma.api.collect.list.List;
import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.api.text.Strings;
import magma.app.TypeCompiler;
import magma.app.ValueCompiler;
import magma.app.WhitespaceCompiler;
import magma.app.compile.CompileState;
import magma.app.compile.compose.Composable;
import magma.app.compile.compose.SplitComposable;
import magma.app.compile.compose.SuffixComposable;
import magma.app.compile.locate.FirstLocator;
import magma.app.compile.merge.Merger;
import magma.app.compile.merge.ValueMerger;
import magma.app.compile.rule.OrRule;
import magma.app.compile.split.LocatingSplitter;
import magma.app.compile.split.Splitter;
import magma.app.compile.type.Type;
import magma.app.compile.type.resolve.ResolvedTypes;

public final class TemplateTypes {
    public static Option<Tuple2<CompileState, Type>> parseGeneric(CompileState state, String input) {
        return new SuffixComposable<Tuple2<CompileState, Type>>(">", (String withoutEnd) -> {
            Splitter splitter = new LocatingSplitter("<", new FirstLocator());
            return new SplitComposable<Tuple2<CompileState, Type>>(splitter, Composable.toComposable((String baseString, String argsString) -> {
                var argsTuple = ValueCompiler.values((CompileState state1, String s) -> TemplateTypes.compileTypeArgument(state1, s)).apply(state, argsString).orElse(new Tuple2Impl<CompileState, List<String>>(state, Lists.empty()));
                var argsState = argsTuple.left();
                var args = argsTuple.right();

                var base = Strings.strip(baseString);
                return TemplateTypes.assembleFunctionType(argsState, base, args).or(() -> {
                    var compileState = ResolvedTypes.addResolvedImportFromCache0(argsState, base);
                    return new Some<Tuple2<CompileState, Type>>(new Tuple2Impl<CompileState, Type>(compileState, new TemplateType(base, args)));
                });
            })).apply(withoutEnd);
        }).apply(Strings.strip(input));
    }

    public static Option<Tuple2<CompileState, Type>> assembleFunctionType(CompileState state, String base, List<String> args) {
        return TemplateTypes.mapFunctionType(base, args).map((Type generated) -> new Tuple2Impl<CompileState, Type>(state, generated));
    }

    private static Option<Type> mapFunctionType(String base, List<String> args) {
        if (Strings.equalsTo("Function", base)) {
            return args.findFirst().and(() -> args.find(1))
                    .map((Tuple2<String, String> tuple) -> new FunctionType(Lists.of(tuple.left()), tuple.right()));
        }

        if (Strings.equalsTo("BiFunction", base)) {
            return args.find(0)
                    .and(() -> args.find(1))
                    .and(() -> args.find(2))
                    .map((Tuple2<Tuple2<String, String>, String> tuple) -> new FunctionType(Lists.of(tuple.left().left(), tuple.left().right()), tuple.right()));
        }

        if (Strings.equalsTo("Supplier", base)) {
            return args.findFirst().map((String first) -> new FunctionType(Lists.empty(), first));
        }

        if (Strings.equalsTo("Consumer", base)) {
            return args.findFirst().map((String first) -> new FunctionType(Lists.of(first), "void"));
        }

        if (Strings.equalsTo("Predicate", base)) {
            return args.findFirst().map((String first) -> new FunctionType(Lists.of(first), "boolean"));
        }

        return new None<Type>();
    }

    public static Option<Tuple2<CompileState, String>> compileTypeArgument(CompileState state, String input) {
        return new OrRule<String>(Lists.of(
                (CompileState state2, String input1) -> WhitespaceCompiler.compileWhitespace(state2, input1),
                (CompileState state1, String type) -> TypeCompiler.createTypeRule()
                        .apply(state1, type)
                        .map((Tuple2<CompileState, Type> tuple) -> new Tuple2Impl<CompileState, String>(tuple.left(), TypeCompiler.generateType(tuple.right())))
        )).apply(state, input);
    }

    public static String generateTemplateType(TemplateType templateType) {
        return templateType.base() + "<" + Merger.generateAll(templateType.args(), new ValueMerger()) + ">";
    }
}
