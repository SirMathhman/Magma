package magma.app;

import jvm.api.collect.list.Lists;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.symbol.Symbols;
import magma.app.compile.type.PrimitiveType;
import magma.app.compile.type.PrimitiveTypes;
import magma.app.compile.type.Type;
import magma.app.compile.type.collect.array.ArrayType;
import magma.app.compile.type.collect.array.ArrayTypes;
import magma.app.compile.type.collect.variadic.VariadicType;
import magma.app.compile.type.collect.variadic.VariadicTypes;
import magma.app.compile.type.resolve.template.FunctionType;
import magma.app.compile.type.resolve.template.FunctionTypes;
import magma.app.compile.type.resolve.template.TemplateType;
import magma.app.compile.type.resolve.template.TemplateTypes;
import magma.app.compile.value.Placeholder;
import magma.app.compile.value.Symbol;

public final class TypeCompiler {
    public static Rule<Type> createTypeRule() {
        return new OrRule<Type>(Lists.of(
                VariadicTypes::parseVariadic,
                TemplateTypes::parseGeneric,
                PrimitiveTypes::parsePrimitive,
                Symbols::parseSymbolType,
                ArrayTypes::parseArray
        ));
    }

    public static String generateType(Type type) {
        return switch (type) {
            case FunctionType functionType -> FunctionTypes.generateFunctionType(functionType);
            case Placeholder placeholder -> Placeholder.fromNode(placeholder);
            case PrimitiveType primitiveType -> PrimitiveTypes.generatePrimitiveType(primitiveType);
            case Symbol symbol -> Symbols.generateSymbol(symbol);
            case TemplateType templateType -> TemplateTypes.generateTemplateType(templateType);
            case VariadicType variadicType -> VariadicTypes.generateVariadicType(variadicType);
            case ArrayType arrayType -> TypeCompiler.generateType(arrayType.childType()) + "[]";
            default -> "?";
        };
    }
}
