import { Composable } from "../../../../magma/app/compile/compose/Composable";
import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
import { Strings } from "../../../../magma/api/text/Strings";
export class PrefixComposable<T> implements Composable<string, T> {
	prefix: string;
	mapper: Composable<string, T>;
	constructor (prefix: string, mapper: Composable<string, T>) {
		this.prefix = prefix;
		this.mapper = mapper;
	}
	apply(input: string): Option<T> {
		if (!!input/*string*/.startsWith(this.prefix())/*unknown*/){
			return new None<T>()/*unknown*/;
		}
		let slice = Strings.sliceFrom(input, Strings.length(this.prefix()))/*unknown*/;
		return this.mapper().apply(slice)/*unknown*/;
	}
}
