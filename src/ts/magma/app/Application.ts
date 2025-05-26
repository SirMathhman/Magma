import { Sources } from "../../magma/app/Sources";
import { Targets } from "../../magma/app/Targets";
import { Source } from "../../magma/app/io/Source";
import { Joiner } from "../../magma/api/collect/Joiner";
import { IOError } from "../../magma/api/io/IOError";
import { Option } from "../../magma/api/option/Option";
import { Platform } from "../../magma/app/Platform";
import { Iterable } from "../../magma/api/collect/list/Iterable";
import { CompileState } from "../../magma/app/compile/CompileState";
import { Result } from "../../magma/api/result/Result";
import { ImmutableCompileState } from "../../magma/app/compile/ImmutableCompileState";
import { Context } from "../../magma/app/compile/Context";
import { Files } from "../../jvm/api/io/Files";
import { Dependency } from "../../magma/app/compile/Dependency";
import { Err } from "../../magma/api/result/Err";
import { RootCompiler } from "../../magma/app/RootCompiler";
import { Ok } from "../../magma/api/result/Ok";
import { Import } from "../../magma/app/compile/Import";
import { Registry } from "../../magma/app/compile/Registry";
class Application {
	sources: Sources;
	targets: Targets;
	constructor (sources: Sources, targets: Targets) {
		this.sources = sources;
		this.targets = targets;
	}
	static formatSource(source: Source): string {
		return "\n\t" + source.createLocation().name() + ": " + Application.joinNamespace(source)/*unknown*/;
	}
	static joinNamespace(source: Source): string {
		return source.createLocation().namespace().iter().collect(new Joiner(".")).orElse("")/*unknown*/;
	}
	runWith(platform: Platform): Option<IOError> {
		return this.sources.listSources().flatMapValue((children: Iterable<Source>) => {
			return this.runWithChildren(platform, children)/*unknown*/;
		}).findError()/*unknown*/;
	}
	runWithChildren(platform: Platform, children: Iterable<Source>): Result<CompileState, IOError> {
		let state: CompileState = ImmutableCompileState.createEmpty().mapContext((context: Context) => {
			return context.withPlatform(platform)/*unknown*/;
		})/*unknown*/;
		let initial = children.iter().foldWithInitial(state, (current: CompileState, source: Source) => {
			return current.mapContext((context1: Context) => {
				return context1.addSource(source)/*unknown*/;
			})/*unknown*/;
		})/*unknown*/;
		let folded = children.iter().foldWithInitialToResult(initial, this.runWithSource)/*unknown*/;
		if (!state/*unknown*/.context().hasPlatform(Platform.PlantUML) || !/*(folded instanceof Ok(var result))*//*unknown*/){
			return folded/*unknown*/;
		}
		let diagramPath = Files.get(".", "diagram.puml")/*unknown*/;
		let joinedDependencies = result.registry().iterDependencies().map((dependency: Dependency) => {
			return dependency.name() + " --> " + dependency.child() + "\n"/*unknown*/;
		}).collect(new Joiner("")).orElse("")/*unknown*/;
		let maybeError = diagramPath.writeString("@startuml\nskinparam linetype ortho\n" + result.registry().output() + joinedDependencies + "@enduml")/*unknown*/;
		if (!/*(maybeError instanceof Some(var error))*//*unknown*/){
			return folded/*unknown*/;
		}
		return new Err<CompileState, IOError>(error)/*unknown*/;
	}
	runWithSource(state: CompileState, source: Source): Result<CompileState, IOError> {
		return source.read().flatMapValue((input: string) => {
			return this.runWithInput(state, source, input)/*unknown*/;
		})/*unknown*/;
	}
	runWithInput(state1: CompileState, source: Source, input: string): Result<CompileState, IOError> {
		let location = source.createLocation()/*unknown*/;
		let compiled = RootCompiler.compileRoot(state1, input, location)/*unknown*/;
		let compiledState = compiled.left()/*unknown*/;
		if (compiledState.context().hasPlatform(Platform.PlantUML)/*unknown*/){
			return new Ok<CompileState, IOError>(compiledState)/*unknown*/;
		}
		let otherOutput = compiled.right()/*unknown*/;
		let joinedImports = compiledState.registry().queryImports().map(Import.generate).collect(new Joiner("")).orElse("")/*unknown*/;
		let joined = joinedImports + compiledState.registry().output() + otherOutput/*unknown*/;
		let cleared = state1.mapRegistry(Registry.reset)/*unknown*/;
		return this.writeTarget(source, cleared, joined)/*unknown*/;
	}
	writeTarget(source: Source, cleared: CompileState, output: string): Result<CompileState, IOError> {
		/*return this.targets().writeSource(source.createLocation(), output)
                .<Result<CompileState, IOError>>map(Err::new)
                .orElseGet(() -> {
                    return new Ok<CompileState, IOError>(cleared);
                })*/;
	}
}
