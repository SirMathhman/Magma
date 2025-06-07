import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple command line interface for the Transpiler.
 */
export default class Main {
    main(args: string[]): void {
        let error: Option<string> = /* TODO */;
        if (/* TODO */) {
            // TODO
        }
    }

    run(): Option<string> {
        let srcRoot: any = /* TODO */;
        let outRoot: any = /* TODO */;
        let files: Result<List<any>> = /* TODO */;
        if (/* TODO */) {
            // TODO
        }
        // TODO
        let err: Option<string> = /* TODO */;
        if (/* TODO */) {
            // TODO
        }
        // TODO
        return /* TODO */;
    }

    listJavaFiles(srcRoot: any): Result<List<any>> {
        let javaFiles: List<any> = /* TODO */;
        let stream: any = /* TODO */;
        // TODO
        if (/* TODO */) {
            // TODO
        }
        // TODO
        return /* TODO */;
        // TODO
        return /* TODO */;
        // TODO
    }

    transpileFile(srcRoot: any, outRoot: any, javaFile: any): Option<string> {
        // TODO
        let javaSrc: string = /* TODO */;
        let ts: string = /* TODO */;
        let rel: any = /* TODO */;
        let name: string = /* TODO */;
        let withoutExt: string = /* TODO */;
        let outFile: any = /* TODO */;
        // TODO
        // TODO
        return /* TODO */;
        // TODO
        return /* TODO */;
        // TODO
    }
}
