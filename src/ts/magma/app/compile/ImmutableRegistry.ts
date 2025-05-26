import { Registry } from "../../../magma/app/compile/Registry";
import { Import } from "../../../magma/app/compile/Import";
import { List } from "../../../magma/api/collect/list/List";
import { Dependency } from "../../../magma/app/compile/Dependency";
import { Lists } from "../../../jvm/api/collect/list/Lists";
import { Iter } from "../../../magma/api/collect/Iter";
export class ImmutableRegistry implements Registry {
	imports: List<Import>;
	output: string;
	dependencies: List<Dependency>;
	constructor (imports: List<Import>, output: string, dependencies: List<Dependency>) {
		this.imports = imports;
		this.output = output;
		this.dependencies = dependencies;
	}
	static createEmpty(): Registry {
		return new ImmutableRegistry(Lists.empty(), "", Lists.empty())/*unknown*/;
	}
	iterDependencies(): Iter<Dependency> {
		return this.dependencies().iter()/*unknown*/;
	}
	doesImportExistAlready(requestedChild: string): boolean {
		return this.imports().iter().filter((node: Import) => {
			return node.hasSameChild(requestedChild)/*unknown*/;
		}).next().isPresent()/*unknown*/;
	}
	queryImports(): Iter<Import> {
		return this.imports().iter()/*unknown*/;
	}
	addDependency(dependency: Dependency): Registry {
		return new ImmutableRegistry(this.imports(), this.output(), this.dependencies().addLast(dependency))/*unknown*/;
	}
	addImport(import_: Import): Registry {
		return new ImmutableRegistry(this.imports().addLast(import_), this.output(), this.dependencies())/*unknown*/;
	}
	append(element: string): Registry {
		return new ImmutableRegistry(this.imports(), this.output() + element, this.dependencies())/*unknown*/;
	}
	containsDependency(dependency: Dependency): boolean {
		return this.dependencies().contains(dependency)/*unknown*/;
	}
	reset(): Registry {
		return new ImmutableRegistry(Lists.empty(), "", this.dependencies())/*unknown*/;
	}
}
