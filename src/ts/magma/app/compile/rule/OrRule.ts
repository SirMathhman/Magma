import { Rule } from "../../../../magma/app/compile/rule/Rule";
import { Iterable } from "../../../../magma/api/collect/list/Iterable";
import { CompileState } from "../../../../magma/app/compile/CompileState";
import { Tuple2 } from "../../../../magma/api/Tuple2";
import { Tuple2Impl } from "../../../../magma/api/Tuple2Impl";
import { Placeholder } from "../../../../magma/app/compile/define/Placeholder";
import { Option } from "../../../../magma/api/option/Option";
import { Iters } from "../../../../magma/api/collect/Iters";
export class OrRule<T> implements Rule<T> {
	rules: Iterable<Rule<T>>;
	constructor (rules: Iterable<Rule<T>>) {
		this.rules = rules;
	}
	static compileOrPlaceholder(state: CompileState, input: string, rules: Iterable<Rule<string>>): Tuple2<CompileState, string> {
		return new OrRule<string>(rules).apply(state, input).orElseGet(() => {
			return new Tuple2Impl<CompileState, string>(state, Placeholder.generatePlaceholder(input))/*unknown*/;
		})/*unknown*/;
	}
	apply(state: CompileState, input: string): Option<Tuple2<CompileState, T>> {
		return this.rules.iter().map((rule: Rule<T>) => {
			return rule.apply(state, input)/*unknown*/;
		}).flatMap(Iters.fromOption).next()/*unknown*/;
	}
}
