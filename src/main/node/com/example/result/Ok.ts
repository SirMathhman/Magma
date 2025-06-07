import None from "../option/None";
import Option from "../option/Option";
import Some from "../option/Some";
/** Successful result value. */
export default class Ok<T> implements Result<T> {
    private readonly value: any;

    Ok(value: any): any {
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
