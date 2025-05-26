import { Stack } from "../../../magma/app/compile/Stack";
import { List } from "../../../magma/api/collect/list/List";
import { Definition } from "../../../magma/app/compile/define/Definition";
import { Lists } from "../../../jvm/api/collect/list/Lists";
import { Option } from "../../../magma/api/option/Option";
import { Strings } from "../../../magma/api/text/Strings";
import { Iterable } from "../../../magma/api/collect/list/Iterable";
export class ImmutableStack implements Stack {
	structureNames: List<string>;
	definitions: List<Definition>;
	constructor (structureNames: List<string>, definitions: List<Definition>) {
		this.structureNames = structureNames;
		this.definitions = definitions;
	}
	static createEmpty(): Stack {
		return new ImmutableStack(Lists.empty(), Lists.empty())/*unknown*/;
	}
	findLastStructureName(): Option<string> {
		return this.structureNames().findLast()/*unknown*/;
	}
	isWithinLast(name: string): boolean {
		return this.findLastStructureName().filter((anObject: string) => {
			return Strings.equalsTo(name, anObject)/*unknown*/;
		}).isPresent()/*unknown*/;
	}
	hasAnyStructureName(base: string): boolean {
		return this.structureNames().iter().anyMatch((inner: string) => {
			return Strings.equalsTo(inner, base)/*unknown*/;
		})/*unknown*/;
	}
	resolveNode(name: string): Option<Definition> {
		return this.definitions().iterReversed().filter((definition: Definition) => {
			return definition.isNamed(name)/*unknown*/;
		}).next()/*unknown*/;
	}
	pushStructureName(name: string): Stack {
		return new ImmutableStack(this.structureNames().addLast(name), this.definitions())/*unknown*/;
	}
	defineAll(definitions: Iterable<Definition>): Stack {
		return new ImmutableStack(this.structureNames(), this.definitions().addAll(definitions))/*unknown*/;
	}
	popStructureName(): Stack {
		return new ImmutableStack(this.structureNames().removeLast().orElse(this.structureNames()), this.definitions())/*unknown*/;
	}
}
