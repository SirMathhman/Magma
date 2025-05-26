import { Composable } from "../../../../magma/app/compile/compose/Composable";
import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
import { Strings } from "../../../../magma/api/text/Strings";
export class SuffixComposable<T> implements Composable<string, T> {
	suffix: string;
	mapper: Composable<string, T>;
	constructor (suffix: string, mapper: Composable<string, T>) {
		this.suffix = suffix;
		this.mapper = mapper;
	}
	apply(input: string): Option<T> {
		if (!input/*string*/.endsWith(this.suffix())/*unknown*/){
			return new None<T>()/*unknown*/;
		}
		let length = Strings.length(input)/*unknown*/;
		let length1 = Strings.length(this.suffix())/*unknown*/;
		let content = Strings.sliceBetween(input, 0, length - length1)/*unknown*/;
		return this.mapper().apply(content)/*unknown*/;
	}
}
