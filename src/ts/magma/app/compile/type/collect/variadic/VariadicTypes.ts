import { CompileState } from "../../../../../../magma/app/compile/CompileState";
import { Type } from "../../../../../../magma/app/compile/type/Type";
import { Tuple2 } from "../../../../../../magma/api/Tuple2";
import { Option } from "../../../../../../magma/api/option/Option";
import { Strings } from "../../../../../../magma/api/text/Strings";
import { SuffixComposable } from "../../../../../../magma/app/compile/compose/SuffixComposable";
import { TypeCompiler } from "../../../../../../magma/app/TypeCompiler";
import { Tuple2Impl } from "../../../../../../magma/api/Tuple2Impl";
import { VariadicType } from "../../../../../../magma/app/compile/type/collect/variadic/VariadicType";
export class VariadicTypes {
	static parseVariadic(state: CompileState, input: string): Option<Tuple2<CompileState, Type>> {
		let stripped = Strings.strip(input)/*unknown*/;
		return new SuffixComposable<Tuple2<CompileState, Type>>("...", (s: string) => {
			return TypeCompiler.createTypeRule().apply(state, s).map((tuple: Tuple2<CompileState, Type>) => new Tuple2Impl<CompileState, Type>(tuple.left(), tuple.right())/*unknown*/).map((child: Tuple2Impl<CompileState, Type>) => new Tuple2Impl<CompileState, Type>(child.left(), new VariadicType(child.right()))/*unknown*/)/*unknown*/;
		}).apply(stripped)/*unknown*/;
	}
	static generateVariadicType(variadicType: VariadicType): string {
		return TypeCompiler.generateType(variadicType.type()) + "[]"/*unknown*/;
	}
}
