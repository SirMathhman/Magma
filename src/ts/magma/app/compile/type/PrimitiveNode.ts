import { Node } from "../../../../magma/app/compile/node/Node";
import { TypeCompiler } from "../../../../magma/app/TypeCompiler";
export class PrimitiveNode implements Node {
	static String: PrimitiveNode = new PrimitiveNode("string");
	static Number: PrimitiveNode = new PrimitiveNode("number");
	static Boolean: PrimitiveNode = new PrimitiveNode("boolean");
	static Var: PrimitiveNode = new PrimitiveNode("var");
	static Void: PrimitiveNode = new PrimitiveNode("void");
	static Unknown: PrimitiveNode = new PrimitiveNode("unknown");
	value: string;
	constructor (value: string) {
		this.value/*unknown*/ = value/*string*/;
	}
	generateNode(): string {
		return this.value/*unknown*/;
	}
	isVar(): boolean {
		return PrimitiveNode.Var === this/*unknown*/;
	}
	generateBeforeName(): string {
		return ""/*unknown*/;
	}
	generateSimple(): string {
		return TypeCompiler.generateNode(this)/*unknown*/;
	}
	is(type: string): boolean {
		return type.equals(this.name().toLowerCase())/*unknown*/;
	}
}
