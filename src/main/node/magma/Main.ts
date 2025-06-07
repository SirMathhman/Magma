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
        let error: Option<string> = new Main().run();
        if (error.isSome()) {
            // TODO
        }
    }

    run(): Option<string> {
        let srcRoot: any = Path.of("src/main/java");
        let outRoot: any = Path.of("src/main/node");
        let files: Result<List<any>> = listJavaFiles(srcRoot);
        if (!files.isOk()) {
            // TODO
        }
        // TODO
        let err: Option<string> = transpileFile(srcRoot, outRoot, file);
        if (err.isSome()) {
            // TODO
        }
        // TODO
        return new None<>();
    }

    listJavaFiles(srcRoot: any): Result<List<any>> {
        let javaFiles: List<any> = new ArrayList<>();
        let stream: any = Files./* TODO */;
        // TODO
        if (p.toString().endsWith(".java")) {
            // TODO
        }
        // TODO
        return new Ok<>(javaFiles);
        } catch(/* TODO */);
        return new Err<>(e.getMessage());
        // TODO
    }

    transpileFile(srcRoot: any, outRoot: any, javaFile: any): Option<string> {
        // TODO
        let javaSrc: string = Files.readString(javaFile);
        let ts: string = new Transpiler().toTypeScript(javaSrc);
        let rel: any = srcRoot.relativize(javaFile);
        let name: string = rel.toString();
        let withoutExt: string = name.substring(0, name.length());
        let outFile: any = outRoot.resolve(withoutExt + ".ts");
        Files.createDirectories(outFile.getParent());
        Files.writeString(outFile, ts + System.lineSeparator());
        return new None<>();
        } catch(/* TODO */);
        return new Some<>(e.getMessage());
        // TODO
    }
}
