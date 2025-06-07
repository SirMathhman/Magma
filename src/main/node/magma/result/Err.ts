import None from "../option/None";
import Option from "../option/Option";
import Some from "../option/Some";
/** Error result with a message. */
export default class Err<T> implements Result<T> {
    private readonly message: string;

    Err(message: string): any {
        // TODO
    }

    @Override
    isOk(): boolean {
        return /* TODO */;
    }

    @Override
    value(): Option<any> {
        return new None<>();
    }

    @Override
    error(): Option<string> {
        return new Some<>(message);
    }
}
