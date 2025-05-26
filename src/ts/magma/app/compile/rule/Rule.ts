import { Node } from "../../../../magma/app/compile/node/Node";
import { Option } from "../../../../magma/api/option/Option";
export interface Rule {
	lex(input: string): Option<Node>;
}
