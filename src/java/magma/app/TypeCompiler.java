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

import java.util.Objects;

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
        if (type instanceof FunctionType) {
            FunctionType functionType = (FunctionType) type;
            return FunctionTypes.generateFunctionType(functionType);
        }
        if (type instanceof Placeholder) {
            Placeholder placeholder = (Placeholder) type;
            return Placeholder.fromNode(placeholder);
        }
        if (type instanceof PrimitiveType) {
            PrimitiveType primitiveType = (PrimitiveType) type;
            return PrimitiveTypes.generatePrimitiveType(primitiveType);
        }
        if (type instanceof Symbol) {
            Symbol symbol = (Symbol) type;
            return Symbols.generateSymbol(symbol);
        }
        if (type instanceof TemplateType) {
            TemplateType templateType = (TemplateType) type;
            return TemplateTypes.generateTemplateType(templateType);
        }
        if (type instanceof VariadicType) {
            VariadicType variadicType = (VariadicType) type;
            return VariadicTypes.generateVariadicType(variadicType);
        }
        if (type instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) type;
            return TypeCompiler.generateType(arrayType.childType()) + "[]";
        }
        return "?";
    }
}
