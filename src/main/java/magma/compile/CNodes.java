package magma.compile;

import magma.compile.rule.StringTokenSequence;
import magma.compile.rule.TokenSequence;
import magma.list.List;
import magma.option.Option;

public class CNodes {
	sealed public interface CType permits Lang.CTemplate, CFunctionPointer, Lang.Identifier, Lang.Invalid, Lang.Pointer {
		TokenSequence toTokens();
	}

	@Tag("functionPointer")
	public record CFunctionPointer(CType returnType, List<CType> paramTypes) implements CType {
		@Override
		public TokenSequence toTokens() {
			final Option<TokenSequence> collected = paramTypes.stream()
					.map(CType::toTokens)
					.collect(new TokenSequenceCollector("_"));
			final TokenSequence paramsPart = collected.orElse(new StringTokenSequence(""));
			return new StringTokenSequence("fn_").appendSequence(paramsPart)
					.appendSlice("_")
					.appendSequence(returnType.toTokens());
		}
	}

	@Tag("cast")
	public record Cast(CType cType, Lang.CExpression cExpression) implements Lang.CExpression {
	}
}
