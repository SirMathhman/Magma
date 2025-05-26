package magma.app.compile.rule;

import magma.api.option.None;
import magma.api.option.Option;
import magma.api.text.Strings;

public record SuffixRule<T>(String suffix, Rule<T> childRule) implements Rule<T> {
    @Override
    public Option<T> lex(String input) {
        if (!input.endsWith(this.suffix)) {
            return new None<T>();
        }

        var length = Strings.length(input);
        var length1 = Strings.length(this.suffix);
        var content = Strings.sliceBetween(input, 0, length - length1);
        return this.childRule.lex(content);
    }
}