import { Folder } from "../../../../magma/app/compile/fold/Folder";
import { DivideState } from "../../../../magma/app/compile/DivideState";
export class StatementsFolder implements Folder {
	apply(state1: DivideState, c: string): DivideState {
		let appended = state1.append(c)/*unknown*/;
		if (";" === c && appended.isLevel()/*unknown*/){
			return appended.advance()/*unknown*/;
		}
		if ("}" === c && appended.isShallow()/*unknown*/){
			return appended.advance().exit()/*unknown*/;
		}
		if ("{" === c || "(" === c/*unknown*/){
			return appended.enter()/*unknown*/;
		}
		if ("}" === c || ")" === c/*unknown*/){
			return appended.exit()/*unknown*/;
		}
		return appended/*unknown*/;
	}
}
