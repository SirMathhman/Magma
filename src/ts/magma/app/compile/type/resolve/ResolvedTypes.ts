import { CompileState } from "../../../../../magma/app/compile/CompileState";
import { Location } from "../../../../../magma/app/Location";
import { Import } from "../../../../../magma/app/compile/Import";
import { Registry } from "../../../../../magma/app/compile/Registry";
import { Source } from "../../../../../magma/app/io/Source";
import { Option } from "../../../../../magma/api/option/Option";
import { Platform } from "../../../../../magma/app/Platform";
import { None } from "../../../../../magma/api/option/None";
import { Dependency } from "../../../../../magma/app/compile/Dependency";
import { Some } from "../../../../../magma/api/option/Some";
import { List } from "../../../../../magma/api/collect/list/List";
export class ResolvedTypes {
	static getState(state: CompileState, location: Location): CompileState {
		let requestedNamespace = location.namespace()/*unknown*/;
		let requestedChild = location.name()/*unknown*/;
		let namespace = fixNamespace(requestedNamespace, state.context().findNamespaceOrEmpty())/*unknown*/;
		if (state.registry().doesImportExistAlready(requestedChild)/*unknown*/){
			return state/*CompileState*/;
		}
		let namespaceWithChild = namespace.addLast(requestedChild)/*unknown*/;
		let anImport = new Import(namespaceWithChild, requestedChild)/*unknown*/;
		return state.mapRegistry((registry: Registry) => registry.addImport(anImport)/*unknown*/)/*unknown*/;
	}
	static addResolvedImportFromCache0(state: CompileState, base: string): CompileState {
		if (state.stack().hasAnyStructureName(base)/*unknown*/){
			return state/*CompileState*/;
		}
		return state.context().findSource(base).map((source: Source) => {
			let location: Location = source.createLocation()/*unknown*/;
			return getCompileState1(state, location).orElseGet(() => getState(state, location)/*unknown*/)/*unknown*/;
		}).orElse(state)/*unknown*/;
	}
	static getCompileState1(immutableCompileState: CompileState, location: Location): Option<CompileState> {
		if (!!immutableCompileState/*CompileState*/.context().hasPlatform(Platform.PlantUML)/*unknown*/){
			return new None<CompileState>()/*unknown*/;
		}
		let name = immutableCompileState.context().findNameOrEmpty()/*unknown*/;
		let dependency = new Dependency(name, location.name())/*unknown*/;
		if (immutableCompileState.registry().containsDependency(dependency)/*unknown*/){
			return new None<CompileState>()/*unknown*/;
		}
		return new Some<CompileState>(immutableCompileState.mapRegistry((registry1: Registry) => registry1.addDependency(dependency)/*unknown*/))/*unknown*/;
	}
	static fixNamespace(requestedNamespace: List<string>, thisNamespace: List<string>): List<string> {
		if (thisNamespace.isEmpty()/*unknown*/){
			return requestedNamespace.addFirst(".")/*unknown*/;
		}
		return addParentSeparator(requestedNamespace, thisNamespace.size())/*unknown*/;
	}
	static addParentSeparator(newNamespace: List<string>, count: number): List<string> {
		let index = 0/*unknown*/;
		let copy = newNamespace/*List<string>*/;
		while (index < count/*unknown*/){
			copy/*unknown*/ = copy.addFirst("..")/*unknown*/;
			index/*unknown*/++;
		}
		return copy/*unknown*/;
	}
}
