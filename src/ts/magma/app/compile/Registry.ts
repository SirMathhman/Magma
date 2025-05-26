import { Dependency } from "../../../magma/app/compile/Dependency";
import { Iter } from "../../../magma/api/collect/Iter";
import { Import } from "../../../magma/app/compile/Import";
export interface Registry {
	iterDependencies(): Iter<Dependency>;
	doesImportExistAlready(requestedChild: string): boolean;
	queryImports(): Iter<Import>;
	addDependency(dependency: Dependency): Registry;
	addImport(import_: Import): Registry;
	append(element: string): Registry;
	containsDependency(dependency: Dependency): boolean;
	output(): string;
	reset(): Registry;
}
