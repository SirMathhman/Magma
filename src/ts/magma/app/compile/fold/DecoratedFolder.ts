import { Folder } from "../../../../magma/app/compile/fold/Folder";
import { DivideState } from "../../../../magma/app/compile/DivideState";
import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
import { Tuple2 } from "../../../../magma/api/Tuple2";
import { Some } from "../../../../magma/api/option/Some";
import { Tuple2Impl } from "../../../../magma/api/Tuple2Impl";
export class DecoratedFolder implements Folder {
	folder: Folder;
	constructor (folder: Folder) {
		this.folder = folder;
	}
	static foldSingleQuotes(state: DivideState, c: string): Option<DivideState> {
		if ("\'" !== c/*unknown*/){
			return new None<DivideState>()/*unknown*/;
		}
		return state.append(c).popAndAppendToTuple().flatMap(DecoratedFolder.foldEscaped).flatMap((state1: DivideState) => {
			return state1.popAndAppendToOption()/*unknown*/;
		})/*unknown*/;
	}
	static foldEscaped(tuple: Tuple2<DivideState, string>): Option<DivideState> {
		let state = tuple.left()/*unknown*/;
		let c = tuple.right()/*unknown*/;
		if ("\\" === c/*unknown*/){
			return state.popAndAppendToOption()/*unknown*/;
		}
		return new Some<DivideState>(state)/*unknown*/;
	}
	static foldDoubleQuotes(state: DivideState, c: string): Option<DivideState> {
		if ("\"" !== c/*unknown*/){
			return new None<DivideState>()/*unknown*/;
		}
		let appended = state.append(c)/*unknown*/;
		while (true/*unknown*/){
			let maybeTuple = appended.popAndAppendToTuple().toTuple(new Tuple2Impl<DivideState, string>(appended, "\0"))/*unknown*/;
			if (!maybeTuple/*unknown*/.left()/*unknown*/){
				break;
			}
			let tuple = maybeTuple.right()/*unknown*/;
			appended/*unknown*/ = tuple.left()/*unknown*/;
			if ("\\" === tuple.right()/*unknown*/){
				appended/*unknown*/ = appended.popAndAppendToOption().orElse(appended)/*unknown*/;
			}
			if ("\"" === tuple.right()/*unknown*/){
				break;
			}
		}
		return new Some<DivideState>(appended)/*unknown*/;
	}
	apply(divideState: DivideState, c: string): DivideState {
		return DecoratedFolder.foldSingleQuotes(divideState, c).or(() => {
			return DecoratedFolder.foldDoubleQuotes(divideState, c)/*unknown*/;
		}).orElseGet(() => {
			return this.folder.apply(divideState, c)/*unknown*/;
		})/*unknown*/;
	}
}
