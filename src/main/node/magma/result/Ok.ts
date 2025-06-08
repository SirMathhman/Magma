import None from "../option/None";
import Option from "../option/Option";
import Some from "../option/Some";
/** Successful result value. */
export default class Ok<T> implements Result<T> {
    private readonly value: T;

    Ok(value: T): public {
        // TODO
    }

    @Override
    isOk(): boolean {
        return /* TODO */;
    }

    @Override
    value(): Option<T> {
        return new Some<T>(value);
    }

    @Override
    error(): Option<string> {
        return new None<string>();
    }
}
