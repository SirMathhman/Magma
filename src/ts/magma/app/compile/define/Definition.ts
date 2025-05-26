import { List } from "../../../../magma/api/collect/list/List";
import { Iterable } from "../../../../magma/api/collect/list/Iterable";
import { Node } from "../../../../magma/app/compile/node/Node";
import { Option } from "../../../../magma/api/option/Option";
import { Some } from "../../../../magma/api/option/Some";
import { Joiner } from "../../../../magma/api/collect/Joiner";
import { TypeCompiler } from "../../../../magma/app/TypeCompiler";
import { RootCompiler } from "../../../../magma/app/RootCompiler";
import { MethodHeader } from "../../../../magma/app/compile/define/MethodHeader";
import { Strings } from "../../../../magma/api/text/Strings";
export class Definition {
	annotations: List<string>;
	modifiers: List<string>;
	typeParams: Iterable<string>;
	type: Node;
	name: string;
	constructor (annotations: List<string>, modifiers: List<string>, typeParams: Iterable<string>, type: Node, name: string) {
		this.annotations = annotations;
		this.modifiers = modifiers;
		this.typeParams = typeParams;
		this.type = type;
		this.name = name;
	}
	findNode(): Node {
		return this.type/*unknown*/;
	}
	toAssignment(): string {
		return "\n\t\tthis." + this.name + " = " + this.name + ";"/*unknown*/;
	}
	generate(): string {
		return this.generateWithAfterName("")/*unknown*/;
	}
	asDefinition(): Option<Definition> {
		return new Some<Definition>(this)/*unknown*/;
	}
	generateWithAfterName(afterName: string): string {
		let joinedNodeParams = this.joinNodeParams()/*unknown*/;
		let joinedModifiers = this.modifiers.iter().map((value: string) => {
			return value + " "/*unknown*/;
		}).collect(new Joiner("")).orElse("")/*unknown*/;
		return joinedModifiers + TypeCompiler.generateBeforeName(this.type) + this.name + joinedNodeParams + afterName + this.generateNode()/*unknown*/;
	}
	generateNode(): string {
		if (this.type.is("var")/*unknown*/){
			return ""/*unknown*/;
		}
		return ": " + TypeCompiler.generateNode(this.type)/*unknown*/;
	}
	joinNodeParams(): string {
		return RootCompiler.joinNodeParams(this.typeParams)/*unknown*/;
	}
	hasAnnotation(annotation: string): boolean {
		return this.annotations.contains(annotation)/*unknown*/;
	}
	removeModifier(modifier: string): MethodHeader {
		return new Definition(this.annotations, this.modifiers.removeNode(modifier), this.typeParams, this.type, this.name)/*unknown*/;
	}
	isNamed(name: string): boolean {
		return Strings.equalsTo(this.name, name)/*unknown*/;
	}
}
