import { FunctionType } from "../../../../../magma/app/compile/type/FunctionType";
import { Tuple2 } from "../../../../../magma/api/Tuple2";
import { Joiner } from "../../../../../magma/api/collect/Joiner";
export class FunctionTypes {
	static generateFunctionType(functionType: FunctionType): string {
		let joinedArguments = functionType.args().iterWithIndices().map((tuple: Tuple2<number, string>) => "arg" + tuple.left() + " : " + tuple.right()/*unknown*/).collect(new Joiner(", ")).orElse("")/*unknown*/;
		return "(" + joinedArguments + ") => " + functionType.returns()/*unknown*/;
	}
}
