import { StatefulRule } from "../../../../magma/app/compile/rule/StatefulRule";
import { Iterable } from "../../../../magma/api/collect/list/Iterable";
import { CompileState } from "../../../../magma/app/compile/CompileState";
import { Tuple2 } from "../../../../magma/api/Tuple2";
import { Tuple2Impl } from "../../../../magma/api/Tuple2Impl";
import { Placeholders } from "../../../../magma/app/compile/define/Placeholders";
import { Option } from "../../../../magma/api/option/Option";
import { Iters } from "../../../../magma/api/collect/Iters";
export class StatefulOrRule<T> implements StatefulRule<T> {
	rules: Iterable<StatefulRule<T>>;
	constructor (rules: Iterable<StatefulRule<T>>) {
		this.rules = rules;
	}
	static compileOrPlaceholder(state: CompileState, input: string, rules: Iterable<StatefulRule<string>>): Tuple2<CompileState, string> {
		return new StatefulOrRule<string>(rules).apply(state, input).orElseGet(() => {
			return new Tuple2Impl<CompileState, string>(state, Placeholders.generatePlaceholder(input))/*unknown*/;
		})/*unknown*/;
	}
	apply(state: CompileState, input: string): Option<Tuple2<CompileState, T>> {
		return this.rules.iter().map((statefulRule: StatefulRule<T>) => statefulRule.apply(state, input)/*unknown*/).flatMap(Iters.fromOption).next()/*unknown*/;
	}
}
