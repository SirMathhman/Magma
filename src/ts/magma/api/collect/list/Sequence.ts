import { Iterable } from "../../../../magma/api/collect/list/Iterable";
import { Option } from "../../../../magma/api/option/Option";
export interface Sequence<T> extends Iterable<T> {
	size(): number;
	findLast(): Option<T>;
	findFirst(): Option<T>;
	find(index: number): Option<T>;
	isEmpty(): boolean;
	contains(element: T): boolean;
}
