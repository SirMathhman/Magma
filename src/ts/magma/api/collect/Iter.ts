import { Collector } from "../../../magma/api/collect/Collector";
import { Option } from "../../../magma/api/option/Option";
import { Result } from "../../../magma/api/result/Result";
export interface Iter<T> {
	collect<C>(collector: Collector<T, C>): C;
	map<R>(mapper: (arg0 : T) => R): Iter<R>;
	foldWithInitial<R>(initial: R, folder: (arg0 : R, arg1 : T) => R): R;
	foldWithMapper<R>(mapper: (arg0 : T) => R, folder: (arg0 : R, arg1 : T) => R): Option<R>;
	flatMap<R>(mapper: (arg0 : T) => Iter<R>): Iter<R>;
	next(): Option<T>;
	allMatch(predicate: (arg0 : T) => boolean): boolean;
	filter(predicate: (arg0 : T) => boolean): Iter<T>;
	anyMatch(predicate: (arg0 : T) => boolean): boolean;
	foldWithInitialToResult<R, X>(initial: R, folder: (arg0 : R, arg1 : T) => Result<R, X>): Result<R, X>;
}
