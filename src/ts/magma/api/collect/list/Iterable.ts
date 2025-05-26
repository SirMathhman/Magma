import { Iter } from "../../../../magma/api/collect/Iter";
import { Tuple2 } from "../../../../magma/api/Tuple2";
export interface Iterable<T> {
	iter(): Iter<T>;
	iterWithIndices(): Iter<Tuple2<number, T>>;
	iterReversed(): Iter<T>;
}
