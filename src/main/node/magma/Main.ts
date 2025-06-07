import Transpiler from "./app/Transpiler";
import None from "./option/None";
import Option from "./option/Option";
import Some from "./option/Some";
import Err from "./result/Err";
import Ok from "./result/Ok";
import Result from "./result/Result";
import IOException from "../java/io/IOException";
import Files from "../java/nio/file/Files";
import Path from "../java/nio/file/Path";
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
        let srcRoot: var = Path.of("src/main/java");
        let outRoot: var = Path.of("src/main/node");
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

    listJavaFiles(srcRoot: Path): Result<List<Path>> {
        let javaFiles: List<Path> = new ArrayList<>();
        let stream: (var = Files./* TODO */;
        // TODO
        if (p.toString().endsWith(".java")) {
            javaFiles.add(p);
        }
        // TODO
        return new Ok<>(javaFiles);
        } catch(/* TODO */);
        return new Err<>(e.getMessage());
        // TODO
    }

    transpileFile(srcRoot: Path, outRoot: Path, javaFile: Path): Option<string> {
        // TODO
        let javaSrc: var = Files.readString(javaFile);
        let ts: var = new Transpiler().toTypeScript(javaSrc);
        let rel: var = srcRoot.relativize(javaFile);
        let name: var = rel.toString();
        let withoutExt: var = name.substring(0, name.length());
        let outFile: var = outRoot.resolve(withoutExt + ".ts");
        Files.createDirectories(outFile.getParent());
        Files.writeString(outFile, ts + System.lineSeparator());
        return new None<>();
        } catch(/* TODO */);
        return new Some<>(e.getMessage());
        // TODO
    }
}
