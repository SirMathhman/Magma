/** Holds a present value. */
export default class Some<T> implements Option<T> {
    private readonly value: any;

    Some(value: any): any {
        // TODO
    }

    @Override
    isSome(): boolean {
        return /* TODO */;
    }

    @Override
    get(): any {
        return value;
    }
}
