import { Iter } from "../../../magma/api/collect/Iter";
import { Tuple2 } from "../../../magma/api/Tuple2";
import { Option } from "../../../magma/api/option/Option";
export interface DivideState {
	query(): Iter<string>;
	advance(): DivideState;
	append(c: string): DivideState;
	isLevel(): boolean;
	enter(): DivideState;
	exit(): DivideState;
	isShallow(): boolean;
	pop(): Option<Tuple2<DivideState, string>>;
	popAndAppendToTuple(): Option<Tuple2<DivideState, string>>;
	popAndAppendToOption(): Option<DivideState>;
	peek(): string;
	startsWith(slice: string): boolean;
}
