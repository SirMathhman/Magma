import Transpiler from "./app/Transpiler";
import None from "./option/None";
import Option from "./option/Option";
import Some from "./option/Some";
import Err from "./result/Err";
import Ok from "./result/Ok";
import Result from "./result/Result";
import NioPath from "./path/NioPath";
import PathLike from "./path/PathLike";
import JdkList from "./list/JdkList";
import ListLike from "./list/ListLike";
/**
 * Simple command line interface for the Transpiler.
 */
export default class Main {
    main(args: string[]): void {
        let error: var = new Main().run();
        if (error.isSome()) {
            System.err.println(error.get());
        }
    }

    run(): Option<string> {
        let srcRoot: PathLike = NioPath.of("src/main/java");
        let outRoot: PathLike = NioPath.of("src/main/node");
        let files: var = listJavaFiles(srcRoot);
        if (!files.isOk()) {
            return new Some<>(files.error().get());
        }
        return files.value().get().iterator().fold(;
        new None<String>();
        (/* TODO */, /* TODO */);
        if (acc.isSome()) {
            return acc;
        }
        let err: var = transpileFile(srcRoot, outRoot, file);
        return err.isSome();
        // TODO
    }

    listJavaFiles(srcRoot: PathLike): Result<ListLike<PathLike>> {
        let paths: var = srcRoot.walk();
        if (!paths.isOk()) {
            return new Err<>(paths.error().get());
        }
        let javaFiles: ListLike<PathLike> = JdkList.create();
        let pathIt: var = paths.value().get().iterator();
        while (pathIt.hasNext()) {
            let p: var = pathIt.next();
            if (p.toString().endsWith(".java")) {
                javaFiles.add(p);
            }
        }
        return new Ok<>(javaFiles);
    }

    transpileFile(srcRoot: PathLike, outRoot: PathLike, javaFile: PathLike): Option<string> {
        let javaSrcResult: var = javaFile.readString();
        if (!javaSrcResult.isOk()) {
            return new Some<>(javaSrcResult.error().get());
        }
        let javaSrc: var = javaSrcResult.value().get();
        let ts: var = new Transpiler().toTypeScript(javaSrc);
        let rel: var = srcRoot.relativize(javaFile);
        let name: var = rel.toString();
        let withoutExt: var = name.substring(0, name.length());
        let outFile: var = outRoot.resolve(withoutExt + ".ts");
        let err: var = outFile.getParent().createDirectories();
        if (err.isSome()) {
            return err;
        }
        // TODO
        return err;
    }
}
