/**
 * Minimal optional value container.
 */
export default class Option<T> {
    private readonly value: any;

    Option(value: any): any {
        // TODO
    }

    some(value: any): Option<any> {
        return /* TODO */;
    }

    none(): Option<any> {
        return /* TODO */;
    }

    isSome(): boolean {
        return /* TODO */;
    }

    isNone(): boolean {
        return /* TODO */;
    }

    get(): any {
        return /* TODO */;
    }
}
