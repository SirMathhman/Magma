import { Sequence } from "../../../../magma/api/collect/list/Sequence";
import { Option } from "../../../../magma/api/option/Option";
import { Iterable } from "../../../../magma/api/collect/list/Iterable";
export interface List<T> extends Sequence<T> {
	addLast(element: T): List<T>;
	subList(startInclusive: number, endExclusive: number): Option<List<T>>;
	addAll(others: Iterable<T>): List<T>;
	addFirst(element: T): List<T>;
	removeNode(element: T): List<T>;
	removeLast(): Option<List<T>>;
}
