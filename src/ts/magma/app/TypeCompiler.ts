import { Type } from "../../magma/app/compile/type/Type";
import { Rule } from "../../magma/app/compile/rule/Rule";
import { OrRule } from "../../magma/app/compile/rule/OrRule";
import { Lists } from "../../jvm/api/collect/list/Lists";
import { VariadicTypes } from "../../magma/app/compile/type/collect/variadic/VariadicTypes";
import { TemplateTypes } from "../../magma/app/compile/type/resolve/template/TemplateTypes";
import { PrimitiveTypes } from "../../magma/app/compile/type/PrimitiveTypes";
import { Symbols } from "../../magma/app/compile/symbol/Symbols";
import { ArrayTypes } from "../../magma/app/compile/type/collect/array/ArrayTypes";
export class TypeCompiler {
	static createTypeRule(): Rule<Type> {
		return new OrRule<Type>(Lists.of(VariadicTypes.parseVariadic, TemplateTypes.parseGeneric, PrimitiveTypes.parsePrimitive, Symbols.parseSymbolType, ArrayTypes.parseArray))/*unknown*/;
	}
	static generateType(type: Type): string {/*return switch (type) {
            case FunctionType functionType -> FunctionTypes.generateFunctionType(functionType);
            case Placeholder placeholder -> Placeholder.fromNode(placeholder);
            case PrimitiveType primitiveType -> PrimitiveTypes.generatePrimitiveType(primitiveType);
            case Symbol symbol -> Symbols.generateSymbol(symbol);
            case TemplateType templateType -> TemplateTypes.generateTemplateType(templateType);
            case VariadicType variadicType -> VariadicTypes.generateVariadicType(variadicType);
            case ArrayType arrayType -> TypeCompiler.generateType(arrayType.childType()) + "[]";
            default -> "?";
        }*/;
	}
}
