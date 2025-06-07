import Transpiler from "./app/Transpiler";
import None from "./option/None";
import Option from "./option/Option";
import Some from "./option/Some";
import Err from "./result/Err";
import Ok from "./result/Ok";
import Result from "./result/Result";
import IOException from "../java/io/IOException";
import Files from "../java/nio/file/Files";
import NioPath from "./path/NioPath";
import PathLike from "./path/PathLike";
import ArrayList from "../java/util/ArrayList";
import List from "../java/util/List";
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
        // TODO
        let err: var = transpileFile(srcRoot, outRoot, file);
        if (err.isSome()) {
            return err;
        }
        // TODO
        return new None<>();
    }

    listJavaFiles(srcRoot: PathLike): Result<List<PathLike>> {
        let paths: var = srcRoot.walk();
        if (!paths.isOk()) {
            return new Err<>(paths.error().get());
        }
        let javaFiles: List<PathLike> = new ArrayList<>();
        // TODO
        if (p.toString().endsWith(".java")) {
            javaFiles.add(p);
        }
        // TODO
        return new Ok<>(javaFiles);
    }

    transpileFile(srcRoot: PathLike, outRoot: PathLike, javaFile: PathLike): Option<string> {
        // TODO
        let javaSrc: var = Files.readString(((NioPath)).toNio());
        let ts: var = new Transpiler().toTypeScript(javaSrc);
        let rel: var = srcRoot.relativize(javaFile);
        let name: var = rel.toString();
        let withoutExt: var = name.substring(0, name.length());
        let outFile: var = outRoot.resolve(withoutExt + ".ts");
        Files.createDirectories(((NioPath).getParent()).toNio());
        Files.writeString(((NioPath)).toNio(), ts + System.lineSeparator());
        return new None<>();
        } catch(/* TODO */);
        return new Some<>(e.getMessage());
        // TODO
    }
}
