/**
 * Simple result type for success or error.
 */
export default class Result<T> {
    private final ok: any;
    private final err: string;

    Result(ok: any, err: string): any {
        // TODO
        // TODO
    }

    ok(value: any): Result<any> {
        // TODO
    }

    error(message: string): Result<any> {
        // TODO
    }

    isOk(): boolean {
        // TODO
    }

    value(): Option<any> {
        // TODO
    }

    error(): Option<string> {
        // TODO
    }
}
