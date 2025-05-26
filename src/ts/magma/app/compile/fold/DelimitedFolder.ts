import { Folder } from "../../../../magma/app/compile/fold/Folder";
import { DivideState } from "../../../../magma/app/compile/DivideState";
export class DelimitedFolder implements Folder {
	delimiter: string;
	constructor (delimiter: string) {
		this.delimiter = delimiter;
	}
	apply(state: DivideState, c: string): DivideState {
		if (this.delimiter() === c/*unknown*/){
			return state.advance()/*unknown*/;
		}
		return state.append(c)/*unknown*/;
	}
}
