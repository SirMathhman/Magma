import { Folder } from "../../../../magma/app/compile/fold/Folder";
import { DivideState } from "../../../../magma/app/compile/DivideState";
export class TypeSeparatorFolder implements Folder {
	apply(state1: DivideState, c: string): DivideState {
		if (" " === c && state1.isLevel()/*unknown*/){
			return state1.advance()/*unknown*/;
		}
		let appended = state1.append(c)/*unknown*/;
		if ("<" === c/*unknown*/){
			return appended.enter()/*unknown*/;
		}
		if (">" === c/*unknown*/){
			return appended.exit()/*unknown*/;
		}
		return appended/*unknown*/;
	}
}
