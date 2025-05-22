import { List } from "../../../../magma/api/collect/list/List";
import { Iterable } from "../../../../magma/api/collect/list/Iterable";
import { Type } from "../../../../magma/app/compile/type/Type";
import { Lists } from "../../../../jvm/api/collect/list/Lists";
import { Joiner } from "../../../../magma/api/collect/Joiner";
import { Option } from "../../../../magma/api/option/Option";
import { Some } from "../../../../magma/api/option/Some";
import { TypeCompiler } from "../../../../magma/app/TypeCompiler";
import { MethodHeader } from "../../../../magma/app/compile/define/MethodHeader";
import { Strings } from "../../../../magma/api/text/Strings";
export class Definition {
	annotations: List<string>;
	modifiers: List<string>;
	typeParams: Iterable<string>;
	type: Type;
	name: string;
	constructor (annotations: List<string>, modifiers: List<string>, typeParams: Iterable<string>, type: Type, name: string) {
		this.annotations = annotations;
		this.modifiers = modifiers;
		this.typeParams = typeParams;
		this.type = type;
		this.name = name;
	}
	static from(type: Type, name: string): Definition {
		return new Definition(Lists.empty(), Lists.empty(), Lists.empty(), type, name)/*unknown*/;
	}
	static joinTypeParams(typeParams: Iterable<string>): string {
		return typeParams.iter().collect(new Joiner(", ")).map((inner: string) => "<" + inner + ">"/*unknown*/).orElse("")/*unknown*/;
	}
	generate(): string {
		return this.generateWithAfterName("")/*unknown*/;
	}
	asDefinition(): Option<Definition> {
		return new Some<Definition>(this)/*unknown*/;
	}
	generateWithAfterName(afterName: string): string {
		let joinedTypeParams = this.joinTypeParams()/*unknown*/;
		let joinedModifiers = this.generateModifiers()/*unknown*/;
		return joinedModifiers + this.type.generateBeforeName() + this.name + joinedTypeParams + afterName + this.generateType()/*unknown*/;
	}
	generateModifiers(): string {
		return this.modifiers.iter().map((value: string) => value + " "/*unknown*/).collect(new Joiner("")).orElse("")/*unknown*/;
	}
	generateType(): string {
		if (this.type.isVar()/*unknown*/){
			return ""/*string*/;
		}
		return ": " + TypeCompiler.generateType(this.type)/*unknown*/;
	}
	joinTypeParams(): string {
		return joinTypeParams(this.typeParams)/*unknown*/;
	}
	hasAnnotation(annotation: string): boolean {
		return this.annotations.contains(annotation)/*unknown*/;
	}
	removeModifier(modifier: string): MethodHeader {
		return new Definition(this.annotations, this.modifiers.removeValue(modifier), this.typeParams, this.type, this.name)/*unknown*/;
	}
	isNamed(name: string): boolean {
		return Strings.equalsTo(this.name, name)/*unknown*/;
	}
}
