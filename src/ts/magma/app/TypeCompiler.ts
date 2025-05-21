import { CompileState } from "../../magma/app/compile/CompileState";
import { Tuple2 } from "../../magma/api/Tuple2";
import { Option } from "../../magma/api/option/Option";
import { Type } from "../../magma/app/compile/type/Type";
import { Tuple2Impl } from "../../magma/api/Tuple2Impl";
import { OrRule } from "../../magma/app/compile/rule/OrRule";
import { Lists } from "../../jvm/api/collect/list/Lists";
import { VariadicTypes } from "../../magma/app/compile/type/collect/variadic/VariadicTypes";
import { TemplateTypes } from "../../magma/app/compile/type/resolve/template/TemplateTypes";
import { PrimitiveTypes } from "../../magma/app/compile/type/PrimitiveTypes";
import { Symbols } from "../../magma/app/compile/symbol/Symbols";
import { ArrayTypes } from "../../magma/app/compile/type/collect/array/ArrayTypes";
export class TypeCompiler {
	static compileType(state: CompileState, type: string): Option<Tuple2<CompileState, string>> {
		return TypeCompiler.parseType(state, type).map((tuple: Tuple2<CompileState, Type>) => new Tuple2Impl<CompileState, string>(tuple.left(), TypeCompiler.generateType(tuple.right()))/*unknown*/)/*unknown*/;
	}
	static parseType(state: CompileState, type: string): Option<Tuple2<CompileState, Type>> {
		return new OrRule<Type>(Lists.of(VariadicTypes.parseVariadic, TemplateTypes.parseGeneric, PrimitiveTypes.parsePrimitive, Symbols.parseSymbolType, ArrayTypes.parseArray)).apply(state, type)/*unknown*/;
	}
	static generateType(type: Type): string {/*return switch (type) {
            case FunctionType functionType -> FunctionTypes.generateFunctionType(functionType);
            case Placeholder placeholder -> Placeholder.generatePlaceholder(placeholder);
            case PrimitiveType primitiveType -> PrimitiveTypes.generatePrimitiveType(primitiveType);
            case Symbol symbol -> Symbols.generateSymbol(symbol);
            case TemplateType templateType -> TemplateTypes.generateTemplateType(templateType);
            case VariadicType variadicType -> VariadicTypes.generateVariadicType(variadicType);
            case ArrayType arrayType -> TypeCompiler.generateType(arrayType.childType()) + "[]";
            default -> "?";
        }*/;
	}
}
