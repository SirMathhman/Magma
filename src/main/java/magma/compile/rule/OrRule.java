package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.Context;
import magma.compile.context.NodeContext;
import magma.compile.context.StringContext;
import magma.compile.error.CompileError;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public record OrRule(List<Rule> rules) implements Rule {
	private record Accumulator<T>(Option<T> option, List<CompileError> errors) {
		public Accumulator() {
			this(new None<>(), new ArrayList<>());
		}

		public Accumulator<T> addError(CompileError error) {
			errors.add(error);
			return this;
		}

		public Accumulator<T> setValue(T value) {
			return new Accumulator<>(new Some<>(value), errors);
		}

		public Result<T, List<CompileError>> toResult() {
			return switch (option) {
				case None<T> _ -> new Err<>(errors);
				case Some<T> v -> new Ok<>(v.value());
			};
		}
	}

	public static Rule Or(Rule... rules) {
		return new OrRule(Arrays.asList(rules));
	}

	@Override
	public Result<Node, CompileError> lex(String content) {
		return foldAll(rule1 -> rule1.lex(content), () -> new StringContext(content));
	}

	private <T> Result<T, CompileError> foldAll(Function<Rule, Result<T, CompileError>> mapper,
																							Supplier<Context> context) {
		return rules.stream()
								.reduce(new Accumulator<T>(), (accumulator, rule) -> switch (mapper.apply(rule)) {
									case Err<T, CompileError> v -> accumulator.addError(v.error());
									case Ok<T, CompileError> v -> accumulator.setValue(v.value());
								}, (_, next) -> next)
								.toResult()
								.mapErr(errors -> new CompileError("No alternative matched for input", context.get(), errors));
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return foldAll(rule1 -> rule1.generate(node), () -> new NodeContext(node));
	}
}
