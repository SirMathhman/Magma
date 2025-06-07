/** Successful result value. */
export default class Ok<T> implements Result<T> {
    private readonly value: any;

    Ok(value: any): void {
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
