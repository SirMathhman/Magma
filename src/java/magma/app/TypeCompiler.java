package magma.app;

import jvm.api.collect.list.Lists;
import magma.api.Tuple2;
import magma.api.Tuple2Impl;
import magma.api.option.Option;
import magma.app.compile.CompileState;
import magma.app.compile.rule.OrRule;
import magma.app.compile.symbol.Symbols;
import magma.app.compile.type.resolve.template.FunctionType;
import magma.app.compile.type.PrimitiveType;
import magma.app.compile.type.PrimitiveTypes;
import magma.app.compile.type.resolve.template.TemplateType;
import magma.app.compile.type.Type;
import magma.app.compile.type.collect.array.ArrayType;
import magma.app.compile.type.collect.array.ArrayTypes;
import magma.app.compile.type.resolve.template.FunctionTypes;
import magma.app.compile.type.resolve.template.TemplateTypes;
import magma.app.compile.type.collect.variadic.VariadicType;
import magma.app.compile.type.collect.variadic.VariadicTypes;
import magma.app.compile.value.Placeholder;
import magma.app.compile.value.Symbol;

public final class TypeCompiler {
    public static Option<Tuple2<CompileState, String>> compileType(CompileState state, String type) {
        return TypeCompiler.parseType(state, type).map((Tuple2<CompileState, Type> tuple) -> new Tuple2Impl<CompileState, String>(tuple.left(), TypeCompiler.generateType(tuple.right())));
    }

    public static Option<Tuple2<CompileState, Type>> parseType(CompileState state, String type) {
        return new OrRule<Type>(Lists.of(
                VariadicTypes::parseVariadic,
                TemplateTypes::parseGeneric,
                PrimitiveTypes::parsePrimitive,
                Symbols::parseSymbolType,
                ArrayTypes::parseArray
        )).apply(state, type);
    }

    public static String generateType(Type type) {
        return switch (type) {
            case FunctionType functionType -> FunctionTypes.generateFunctionType(functionType);
            case Placeholder placeholder -> Placeholder.generatePlaceholder(placeholder);
            case PrimitiveType primitiveType -> PrimitiveTypes.generatePrimitiveType(primitiveType);
            case Symbol symbol -> Symbols.generateSymbol(symbol);
            case TemplateType templateType -> TemplateTypes.generateTemplateType(templateType);
            case VariadicType variadicType -> VariadicTypes.generateVariadicType(variadicType);
            case ArrayType arrayType -> TypeCompiler.generateType(arrayType.childType()) + "[]";
            default -> "?";
        };
    }
}
