import { Composable } from "../../../../magma/app/compile/compose/Composable";
import { Option } from "../../../../magma/api/option/Option";
export class StripComposable<T> implements Composable<string, T> {
	composable: Composable<string, T>;
	constructor (composable: Composable<string, T>) {
		this.composable = composable;
	}
	apply(s: string): Option<T> {
		return this.composable.apply(s.strip())/*unknown*/;
	}
}
