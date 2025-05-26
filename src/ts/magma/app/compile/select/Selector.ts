import { Tuple2 } from "../../../../magma/api/Tuple2";
import { Option } from "../../../../magma/api/option/Option";
import { List } from "../../../../magma/api/collect/list/List";
export interface Selector {
	select(divisions: List<string>): Option<Tuple2<string, string>>;
}
