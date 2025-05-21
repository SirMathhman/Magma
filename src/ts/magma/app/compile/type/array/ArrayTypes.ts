import { CompileState } from "../../../../../magma/app/compile/CompileState";
import { Type } from "../../../../../magma/app/compile/type/Type";
import { Tuple2 } from "../../../../../magma/api/Tuple2";
import { Option } from "../../../../../magma/api/option/Option";
export class ArrayTypes {
	static parseArray(state: CompileState, input: string): Option<Tuple2<CompileState, Type>> {
		/*return new StripComposable<Tuple2<CompileState, Type>>(new SuffixComposable<Tuple2<CompileState, Type>>("[]", (Composable<String, Tuple2<CompileState, Type>>) (String childString) -> TypeCompiler.parseType(state, childString).map(child -> {
            return new Tuple2Impl<CompileState, Type>(child.left(), new ArrayType(child.right()));
        }))).apply(input)*/;
	}
}
