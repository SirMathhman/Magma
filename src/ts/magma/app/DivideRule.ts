import { List } from "../../magma/api/collect/list/List";
import { StatefulRule } from "../../magma/app/compile/rule/StatefulRule";
import { Folder } from "../../magma/app/compile/fold/Folder";
import { StatefulRuleAlias } from "../../magma/app/compile/rule/StatefulRuleAlias";
import { CompileState } from "../../magma/app/compile/CompileState";
import { Tuple2 } from "../../magma/api/Tuple2";
import { Some } from "../../magma/api/option/Some";
import { Option } from "../../magma/api/option/Option";
import { FoldedDivider } from "../../magma/app/compile/divide/FoldedDivider";
import { DecoratedFolder } from "../../magma/app/compile/fold/DecoratedFolder";
import { Tuple2Impl } from "../../magma/api/Tuple2Impl";
import { Lists } from "../../jvm/api/collect/list/Lists";
export class DivideRule<T> implements StatefulRule<List<T>> {
	folder: Folder;
	statefulRule: StatefulRuleAlias<T>;
	constructor (folder: Folder, statefulRule: StatefulRuleAlias<T>) {
		this.folder = folder;
		this.statefulRule = statefulRule;
	}
	static toRule(mapper: (arg0 : CompileState, arg1 : string) => Tuple2<CompileState, string>): StatefulRuleAlias<string> {
		return (state1: CompileState, s: string) => {
			return new Some<Tuple2<CompileState, string>>(mapper(state1, s))/*unknown*/;
		}/*unknown*/;
	}
	apply(state: CompileState, input: string): Option<Tuple2<CompileState, List<T>>> {
		return new FoldedDivider(new DecoratedFolder(this.folder())).divide(input).foldWithInitial(new Some<Tuple2<CompileState, List<T>>>(new Tuple2Impl<CompileState, List<T>>(state, Lists.empty())), (maybeCurrent: Option<Tuple2<CompileState, List<T>>>, segment: string) => {
			return maybeCurrent.flatMap((current: Tuple2<CompileState, List<T>>) => {
				let currentState = current.left()/*unknown*/;
				let currentElement = current.right()/*unknown*/;
				return this.statefulRule().apply(currentState, segment).map((mappedTuple: Tuple2<CompileState, T>) => {
					let mappedState = mappedTuple.left()/*unknown*/;
					let mappedElement = mappedTuple.right()/*unknown*/;
					return new Tuple2Impl<CompileState, List<T>>(mappedState, currentElement.addLast(mappedElement))/*unknown*/;
				})/*unknown*/;
			})/*unknown*/;
		})/*unknown*/;
	}
}
