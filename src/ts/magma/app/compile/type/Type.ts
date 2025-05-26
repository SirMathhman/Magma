import { Node } from "../../../../magma/app/compile/node/Node";
export interface Type extends Node {
	generate(): string;
	generateBeforeName(): string;
	generateSimple(): string;
}
