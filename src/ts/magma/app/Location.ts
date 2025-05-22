import { List } from "../../magma/api/collect/list/List";
import { Iter } from "../../magma/api/collect/Iter";
import { Strings } from "../../magma/api/text/Strings";
export class Location {
	namespace: List<string>;
	name: string;
	constructor (namespace: List<string>, name: string) {
		this.namespace = namespace;
		this.name = name;
	}
	attachExtension(extension: string): string {
		return this.name + "." + extension/*unknown*/;
	}
	iterNamespace(): Iter<string> {
		return this.namespace.iter()/*unknown*/;
	}
	hasName(name: string): boolean {
		return Strings.equalsTo(this.name, name)/*unknown*/;
	}
}
