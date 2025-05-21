import { List } from "../../magma/api/collect/list/List";
import { Rule } from "../../magma/app/compile/rule/Rule";
import { Folder } from "../../magma/app/compile/fold/Folder";
import { CompileState } from "../../magma/app/compile/CompileState";
import { Tuple2 } from "../../magma/api/Tuple2";
import { Option } from "../../magma/api/option/Option";
import { FoldedDivider } from "../../magma/app/compile/divide/FoldedDivider";
import { DecoratedFolder } from "../../magma/app/compile/fold/DecoratedFolder";
import { Some } from "../../magma/api/option/Some";
import { Tuple2Impl } from "../../magma/api/Tuple2Impl";
import { Lists } from "../../jvm/api/collect/list/Lists";
export class DivideRule<T> implements Rule<List<T>> {
	folder: Folder;
	rule: Rule<T>;
	constructor (folder: Folder, rule: Rule<T>) {
		this.folder = folder;
		this.rule = rule;
	}
	apply(state: CompileState, input: string): Option<Tuple2<CompileState, List<T>>> {
		return new FoldedDivider(new DecoratedFolder(this.folder())).divide(input).foldWithInitial(new Some<Tuple2<CompileState, List<T>>>(new Tuple2Impl<CompileState, List<T>>(state, Lists.empty())), (maybeCurrent: Option<Tuple2<CompileState, List<T>>>, segment: string) => maybeCurrent.flatMap((current: Tuple2<CompileState, List<T>>) => {
			let currentState = current.left()/*unknown*/;
			let currentElement = current.right()/*unknown*/;
			return this.rule().apply(currentState, segment).map((mappedTuple: Tuple2<CompileState, T>) => {
				let mappedState = mappedTuple.left()/*unknown*/;
				let mappedElement = mappedTuple.right()/*unknown*/;
				return new Tuple2Impl<CompileState, List<T>>(mappedState, currentElement.addLast(mappedElement))/*unknown*/;
			})/*unknown*/;
		})/*unknown*/)/*unknown*/;
	}
}
