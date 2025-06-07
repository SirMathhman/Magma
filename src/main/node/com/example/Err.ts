/** Error result with a message. */
export default class Err<T> implements Result<T> {
    private readonly message: string;

    Err(message: string): void {
        // TODO
    }

    @Override
    isOk(): boolean {
        return /* TODO */;
    }

    @Override
    value(): Option<any> {
        return /* TODO */;
    }

    @Override
    error(): Option<string> {
        return /* TODO */;
    }
}
