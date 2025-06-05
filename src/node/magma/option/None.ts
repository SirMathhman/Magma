// Auto-generated from magma/option/None.java
import { Option } from "./Option";
export class None<T> implements Option<T> {
	isPresent(): boolean {
		return false;
	}
	get(): T {
		throw new java.util.NoSuchElementException("No value present");
	}
}
