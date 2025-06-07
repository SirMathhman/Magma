/**
 * Simple result type for success or error.
 */
export default class Result<T> {
    private readonly ok: any;
    private readonly err: string;

    Result(ok: any, err: string): any {
        // TODO
        // TODO
    }

    ok(value: any): Result<any> {
        return /* TODO */;
    }

    error(message: string): Result<any> {
        return /* TODO */;
    }

    isOk(): boolean {
        return /* TODO */;
    }

    value(): Option<any> {
        return /* TODO */;
    }

    error(): Option<string> {
        return /* TODO */;
    }
}
