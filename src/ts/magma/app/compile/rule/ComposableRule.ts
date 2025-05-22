import { Value } from "../../../../magma/app/compile/value/Value";
import { Rule } from "../../../../magma/app/compile/rule/Rule";
import { CompileState } from "../../../../magma/app/compile/CompileState";
import { Tuple2 } from "../../../../magma/api/Tuple2";
import { Composable } from "../../../../magma/app/compile/compose/Composable";
import { Option } from "../../../../magma/api/option/Option";
export class ComposableRule implements Rule<Value> {
	mapper: (arg0 : CompileState) => Composable<string, Tuple2<CompileState, Value>>;
	constructor (mapper: (arg0 : CompileState) => Composable<string, Tuple2<CompileState, Value>>) {
		this.mapper = mapper;
	}
	apply(state: CompileState, input: string): Option<Tuple2<CompileState, Value>> {
		return this.mapper.apply(state).apply(input)/*unknown*/;
	}
}
