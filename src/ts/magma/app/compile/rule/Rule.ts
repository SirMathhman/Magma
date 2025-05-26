import { Option } from "../../../../magma/api/option/Option";
export interface Rule<T> {
	lex(input: string): Option<T>;
}
