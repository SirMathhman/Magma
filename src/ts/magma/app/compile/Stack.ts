import { Option } from "../../../magma/api/option/Option";
import { Definition } from "../../../magma/app/compile/define/Definition";
import { Iterable } from "../../../magma/api/collect/list/Iterable";
import { List } from "../../../magma/api/collect/list/List";
export interface Stack {
	findLastStructureName(): Option<string>;
	isWithinLast(name: string): boolean;
	hasAnyStructureName(base: string): boolean;
	resolveValue(name: string): Option<Definition>;
	pushStructureName(name: string): Stack;
	defineAll(definitions: Iterable<Definition>): Stack;
	popStructureName(): Stack;
	findLastDefinitions(): List<Definition>;
	define(definition: Definition): Stack;
}
