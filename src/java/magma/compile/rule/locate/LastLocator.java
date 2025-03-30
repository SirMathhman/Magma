package magma.compile.rule.locate;

import magma.option.None;
import magma.option.Option;
import magma.option.Some;

public class LastLocator implements Locator {
    @Override
    public Option<Integer> locate(String input, String infix) {
        int index = input.lastIndexOf(infix);
        return index == -1 ? new None<Integer>() : new Some<Integer>(index);
    }
}