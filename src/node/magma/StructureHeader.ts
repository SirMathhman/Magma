
/*import java.util.List;*/
/*import java.util.Optional;*/
class StructureHeader/*implements StructureDefinition*/ {
	/*@Override
    public*/ generate(/**/) : string {
		/*final*/ generated : any = this.maybeAfterImplements().map(afterImplements => Placeholder.generate("implements " + afterImplements)).orElse("");
		return this.type + " " + this.name() + generated;/*
    */}
	/**/}

