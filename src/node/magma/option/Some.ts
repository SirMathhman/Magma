// Auto-generated from magma/option/Some.java
import { Option } from "./Option";
export class Some<T> implements Option<T> {
	isPresent(): boolean {
		return true;
	}
	get(): T {
		return value;
	}
}
