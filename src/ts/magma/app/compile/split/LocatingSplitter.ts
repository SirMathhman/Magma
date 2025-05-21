import { Splitter } from "../../../../magma/app/compile/split/Splitter";
import { Locator } from "../../../../magma/app/compile/locate/Locator";
import { Tuple2 } from "../../../../magma/api/Tuple2";
import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
import { Strings } from "../../../../magma/api/text/Strings";
import { Some } from "../../../../magma/api/option/Some";
import { Tuple2Impl } from "../../../../magma/api/Tuple2Impl";
export class LocatingSplitter implements Splitter {
	infix: string;
	locator: Locator;
	constructor (infix: string, locator: Locator) {
		this.infix = infix;
		this.locator = locator;
	}
	apply(input: string): Option<Tuple2<string, string>> {
		let index = this.locator.apply(input, this.infix)/*unknown*/;
		if (0 > index/*unknown*/){
			return new None<Tuple2<string, string>>()/*unknown*/;
		}
		let left = Strings.sliceBetween(input, 0, index)/*unknown*/;
		let length = Strings.length(this.infix)/*unknown*/;
		let right = Strings.sliceFrom(input, index + length)/*unknown*/;
		return new Some<Tuple2<string, string>>(new Tuple2Impl<string, string>(left, right))/*unknown*/;
	}
}
