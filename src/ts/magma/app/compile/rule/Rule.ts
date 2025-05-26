import { CompileState } from "../../../../magma/app/compile/CompileState";
import { Tuple2 } from "../../../../magma/api/Tuple2";
import { Option } from "../../../../magma/api/option/Option";
export interface Rule<T> {
	apply(state: CompileState, input: string): Option<Tuple2<CompileState, T>>;
}
