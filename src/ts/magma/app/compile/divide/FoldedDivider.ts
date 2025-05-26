import { Divider } from "../../../../magma/app/compile/divide/Divider";
import { Folder } from "../../../../magma/app/compile/fold/Folder";
import { Iter } from "../../../../magma/api/collect/Iter";
import { DivideState } from "../../../../magma/app/compile/DivideState";
import { Tuple2Impl } from "../../../../magma/api/Tuple2Impl";
export class FoldedDivider implements Divider {
	folder: Folder;
	constructor (folder: Folder) {
		this.folder = folder;
	}
	divide(input: string): Iter<string> {
		let current = /* (DivideState) new ImmutableDivideState(Lists.empty(), "", 0, input, 0)*/;
		while (true/*unknown*/){
			let poppedTuple0 = current.pop().toTuple(new Tuple2Impl<DivideState, string>(current, "\0"))/*unknown*/;
			if (!poppedTuple0/*unknown*/.left()/*unknown*/){
				break;
			}
			let poppedTuple = poppedTuple0.right()/*unknown*/;
			let poppedState = poppedTuple.left()/*unknown*/;
			let popped = poppedTuple.right()/*unknown*/;
			current/*unknown*/ = this.folder.apply(poppedState, popped)/*unknown*/;
		}
		return current.advance().query()/*unknown*/;
	}
}
