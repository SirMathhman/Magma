import { Type } from "../../magma/app/compile/type/Type";
import { Rule } from "../../magma/app/compile/rule/Rule";
import { OrRule } from "../../magma/app/compile/rule/OrRule";
import { Lists } from "../../jvm/api/collect/list/Lists";
import { VariadicTypes } from "../../magma/app/compile/type/collect/variadic/VariadicTypes";
import { TemplateTypes } from "../../magma/app/compile/type/resolve/template/TemplateTypes";
import { PrimitiveTypes } from "../../magma/app/compile/type/PrimitiveTypes";
import { Symbols } from "../../magma/app/compile/symbol/Symbols";
import { ArrayTypes } from "../../magma/app/compile/type/collect/array/ArrayTypes";
import { FunctionType } from "../../magma/app/compile/type/resolve/template/FunctionType";
import { FunctionTypes } from "../../magma/app/compile/type/resolve/template/FunctionTypes";
import { Placeholder } from "../../magma/app/compile/value/Placeholder";
import { PrimitiveType } from "../../magma/app/compile/type/PrimitiveType";
import { Symbol } from "../../magma/app/compile/value/Symbol";
import { TemplateType } from "../../magma/app/compile/type/resolve/template/TemplateType";
import { VariadicType } from "../../magma/app/compile/type/collect/variadic/VariadicType";
import { ArrayType } from "../../magma/app/compile/type/collect/array/ArrayType";
export class TypeCompiler {
	static createTypeRule(): Rule<Type> {
		return new OrRule<Type>(Lists.of(VariadicTypes.parseVariadic, TemplateTypes.parseGeneric, PrimitiveTypes.parsePrimitive, Symbols.parseSymbolType, ArrayTypes.parseArray))/*unknown*/;
	}
	static generateType(type: Type): string {
		if (type._variant === FunctionType._variantKey/*unknown*/){
			let functionType: FunctionType = /* (FunctionType) type*/;
			return FunctionTypes.generateFunctionType(functionType)/*unknown*/;
		}
		if (type._variant === Placeholder._variantKey/*unknown*/){
			let placeholder: Placeholder = /* (Placeholder) type*/;
			return Placeholder.fromNode(placeholder)/*unknown*/;
		}
		if (type._variant === PrimitiveType._variantKey/*unknown*/){
			let primitiveType: PrimitiveType = /* (PrimitiveType) type*/;
			return PrimitiveTypes.generatePrimitiveType(primitiveType)/*unknown*/;
		}
		if (type._variant === Symbol._variantKey/*unknown*/){
			let symbol: Symbol = /* (Symbol) type*/;
			return Symbols.generateSymbol(symbol)/*unknown*/;
		}
		if (type._variant === TemplateType._variantKey/*unknown*/){
			let templateType: TemplateType = /* (TemplateType) type*/;
			return TemplateTypes.generateTemplateType(templateType)/*unknown*/;
		}
		if (type._variant === VariadicType._variantKey/*unknown*/){
			let variadicType: VariadicType = /* (VariadicType) type*/;
			return VariadicTypes.generateVariadicType(variadicType)/*unknown*/;
		}
		if (type._variant === ArrayType._variantKey/*unknown*/){
			let arrayType: ArrayType = /* (ArrayType) type*/;
			return TypeCompiler.generateType(arrayType.childType()) + "[]"/*unknown*/;
		}
		return "?"/*string*/;
	}
}
