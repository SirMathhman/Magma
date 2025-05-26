import { Files } from "../../jvm/api/io/Files";
import { PathSources } from "../../magma/app/PathSources";
import { PathTargets } from "../../magma/app/PathTargets";
import { Application } from "../../magma/app/Application";
import { IOError } from "../../magma/api/io/IOError";
import { Console } from "../../magma/api/io/Console";
import { Option } from "../../magma/api/option/Option";
import { Iters } from "../../magma/api/collect/Iters";
import { Platform } from "../../magma/app/Platform";
class Main {
	static main(): void {
		let sourceDirectory = Files.get(".", "src", "java")/*unknown*/;
		let sources = new PathSources(sourceDirectory)/*unknown*/;
		let targets = new PathTargets(Files.get(".", "src", "ts"))/*unknown*/;
		Main.run(new Application(sources, targets)).map((error: IOError) => {
			return error.display()/*unknown*/;
		}).ifPresent((displayed: string) => {
			Console.printErrLn(displayed)/*unknown*/;
		})/*unknown*/;
	}
	static run(application: Application): Option<IOError> {
		return Iters.fromArray(Platform.values()).map((platform: Platform) => {
			return application.runWith(platform)/*unknown*/;
		}).flatMap(Iters.fromOption).next()/*unknown*/;
	}
}
