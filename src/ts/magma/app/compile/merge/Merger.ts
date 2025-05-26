import { CompileState } from "../../../../magma/app/compile/CompileState";
import { Tuple2 } from "../../../../magma/api/Tuple2";
import { List } from "../../../../magma/api/collect/list/List";
import { Tuple2Impl } from "../../../../magma/api/Tuple2Impl";
import { Iterable } from "../../../../magma/api/collect/list/Iterable";
export interface Merger {
	static generateAllFromTuple(state: CompileState, elements: List<string>, merger: Merger): Tuple2<CompileState, string> {
		return new Tuple2Impl<CompileState, string>(state, generateAll(elements, merger))/*unknown*/;
	}
	static generateAll(elements: Iterable<string>, merger: Merger): string {
		return elements.iter().foldWithInitial("", merger.merge)/*unknown*/;
	}
	merge(s: string, s2: string): string;
}
