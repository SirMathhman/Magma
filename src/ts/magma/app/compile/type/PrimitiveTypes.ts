import { CompileState } from "../../../../magma/app/compile/CompileState";
import { Type } from "../../../../magma/app/compile/type/Type";
import { Tuple2 } from "../../../../magma/api/Tuple2";
import { Option } from "../../../../magma/api/option/Option";
import { Strings } from "../../../../magma/api/text/Strings";
import { Tuple2Impl } from "../../../../magma/api/Tuple2Impl";
import { Some } from "../../../../magma/api/option/Some";
import { PrimitiveType } from "../../../../magma/app/compile/type/PrimitiveType";
import { None } from "../../../../magma/api/option/None";
export class PrimitiveTypes {
	static parsePrimitive(state: CompileState, input: string): Option<Tuple2<CompileState, Type>> {
		return PrimitiveTypes.findPrimitiveValue(Strings.strip(input)).map((result: Type) => new Tuple2Impl<CompileState, Type>(state, result)/*unknown*/)/*unknown*/;
	}
	static findPrimitiveValue(input: string): Option<Type> {
		let stripped = Strings.strip(input)/*unknown*/;
		if (Strings.equalsTo("char", stripped) || Strings.equalsTo("Character", stripped) || Strings.equalsTo("String", stripped)/*unknown*/){
			return new Some<Type>(PrimitiveType.String)/*unknown*/;
		}
		if (Strings.equalsTo("int", stripped) || Strings.equalsTo("Integer", stripped)/*unknown*/){
			return new Some<Type>(PrimitiveType.Number)/*unknown*/;
		}
		if (Strings.equalsTo("boolean", stripped) || Strings.equalsTo("Boolean", stripped)/*unknown*/){
			return new Some<Type>(PrimitiveType.Boolean)/*unknown*/;
		}
		if (Strings.equalsTo("var", stripped)/*unknown*/){
			return new Some<Type>(PrimitiveType.Var)/*unknown*/;
		}
		if (Strings.equalsTo("void", stripped)/*unknown*/){
			return new Some<Type>(PrimitiveType.Void)/*unknown*/;
		}
		return new None<Type>()/*unknown*/;
	}
	static generatePrimitiveType(primitiveType: PrimitiveType): string {
		return primitiveType.value/*unknown*/;
	}
}
