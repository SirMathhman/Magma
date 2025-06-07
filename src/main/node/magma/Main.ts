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
        if (error./* TODO */()) {
            // TODO
        }
    }

    run(): Option<string> {
        let srcRoot: any = Path./* TODO */("src/main/java");
        let outRoot: any = Path./* TODO */("src/main/node");
        let files: Result<List<any>> = /* TODO */(/* TODO */);
        if (!files./* TODO */()) {
            // TODO
        }
        // TODO
        let err: Option<string> = /* TODO */(/* TODO */, /* TODO */, /* TODO */);
        if (err./* TODO */()) {
            // TODO
        }
        // TODO
        return new None<>();
    }

    listJavaFiles(srcRoot: any): Result<List<any>> {
        let javaFiles: List<any> = new ArrayList<>();
        let stream: any = Files./* TODO */;
        // TODO
        if (p./* TODO */()./* TODO */(".java")) {
            // TODO
        }
        // TODO
        return new Ok<>(/* TODO */);
        /* TODO */(/* TODO */);
        return new Err<>(e.getMessage());
        // TODO
    }

    transpileFile(srcRoot: any, outRoot: any, javaFile: any): Option<string> {
        // TODO
        let javaSrc: string = Files./* TODO */(/* TODO */);
        let ts: string = new Transpiler().toTypeScript(javaSrc);
        let rel: any = srcRoot./* TODO */(/* TODO */);
        let name: string = rel./* TODO */();
        let withoutExt: string = name./* TODO */(0, /* TODO */);
        let outFile: any = outRoot./* TODO */(withoutExt + ".ts");
        /* TODO */(/* TODO */);
        /* TODO */(/* TODO */, /* TODO */);
        return new None<>();
        /* TODO */(/* TODO */);
        return new Some<>(e.getMessage());
        // TODO
    }
}
