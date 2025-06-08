import Transpiler from "./app/Transpiler";
import JdkList from "./list/JdkList";
import ListLike from "./list/ListLike";
import None from "./option/None";
import Option from "./option/Option";
import Some from "./option/Some";
import NioPath from "./path/NioPath";
import PathLike from "./path/PathLike";
import Err from "./result/Err";
import Ok from "./result/Ok";
import Result from "./result/Result";
/**
 * Simple command line interface for the Transpiler.
 */
export default class Main {
    main(args: string[]): void {
        let error : Option<string> = new Main().run();
        if (error.isSome()) {
            System.err.println(error.get());
        }
    }

    run(): Option<string> {
        let srcRoot : PathLike = NioPath.of("src/main/java");
        let outRoot : PathLike = NioPath.of("src/main/node");
        let files : Result<ListLike<PathLike>> = listJavaFiles(srcRoot);
        if (!files.isOk()) {
            return new Some<string>(files.error().get());
        }
        return files.value().get().iterator().fold(new None<String>(), (acc : Option<string>, file : PathLike) => {
            if (acc.isSome()) {
                return acc;
            }
            let err : Option<string> = transpileFile(srcRoot, outRoot, file);
            return err.isSome();
        });
    }

    listJavaFiles(srcRoot: PathLike): Result<ListLike<PathLike>> {
        let paths : unknown = srcRoot.walk();
        if (!paths.isOk()) {
            return new Err<ListLike<PathLike>>(paths.error().get());
        }
        let javaFiles : ListLike<PathLike> = JdkList.create();
        let pathIt : unknown = paths.value().get().iterator();
        while (pathIt.hasNext()) {
            let p : unknown = pathIt.next();
            if (p.toString().endsWith(".java")) {
                javaFiles.add(p);
            }
        }
        return new Ok<ListLike<PathLike>>(javaFiles);
    }

    transpileFile(srcRoot: PathLike, outRoot: PathLike, javaFile: PathLike): Option<string> {
        let javaSrcResult : unknown = javaFile.readString();
        if (!javaSrcResult.isOk()) {
            return new Some<string>(javaSrcResult.error().get());
        }
        let javaSrc : unknown = javaSrcResult.value().get();
        let ts : Transpiler = new Transpiler().toTypeScript(javaSrc);
        let rel : unknown = srcRoot.relativize(javaFile);
        let name : unknown = rel.toString();
        let withoutExt : unknown = name.substring(0, name.length());
        let outFile : unknown = outRoot.resolve(withoutExt + ".ts");
        let err : unknown = outFile.getParent().createDirectories();
        if (err.isSome()) {
            return err;
        }
        // TODO
        return err;
    }
}
