import { Folder } from "../../../../magma/app/compile/fold/Folder";
import { DivideState } from "../../../../magma/app/compile/DivideState";
import { Strings } from "../../../../magma/api/text/Strings";
import { Tuple2 } from "../../../../magma/api/Tuple2";
export class OperatorFolder implements Folder {
	infix: string;
	constructor (infix: string) {
		this.infix/*unknown*/ = infix/*string*/;
	}
	apply(state: DivideState, c: string): DivideState {
		if (c === this.infix.charAt(0) && state.startsWith(Strings.sliceFrom(this.infix, 1))/*unknown*/){
			let length = Strings.length(this.infix) - 1/*unknown*/;
			let counter = 0/*unknown*/;
			let current = state/*DivideState*/;
			while (counter < length/*unknown*/){
				counter/*unknown*/++;
				current/*unknown*/ = current.pop().map((tuple: Tuple2<DivideState, string>) => {
					return tuple.left()/*unknown*/;
				}).orElse(current)/*unknown*/;
			}
			return current.advance()/*unknown*/;
		}
		return state.append(c)/*unknown*/;
	}
}
