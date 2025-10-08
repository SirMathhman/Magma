package magma.compile;

import magma.compile.rule.TokenSequence;
import magma.list.Joiner;
import magma.list.List;

public class CNodes {
	sealed public interface CType permits Lang.CTemplate, CFunctionPointer, Lang.Identifier, Lang.Invalid, Lang.Pointer {
		TokenSequence toTokens();
	}

	@Tag("functionPointer")
	public record CFunctionPointer(CType returnType, List<CType> paramTypes) implements CType {
		@Override
		public TokenSequence toTokens() {
			return "fn_" + paramTypes.stream().map(CType::toTokens).collect(new Joiner("_")) + "_" + returnType.toTokens();
		}
	}

	@Tag("cast")
	public record Cast(CType cType, Lang.CExpression cExpression) implements Lang.CExpression {}
}
