/*package magma;*/
/*import java.util.List;*/
/*import java.util.Optional;*/
/*public*/class StructureHeader/*implements StructureDefinition*/ {
	/*@Override
    public*/ generate(/**/) : string {
		/*final*/ generated : any = this.maybeAfterImplements(/*)
                                  .map(afterImplements -> Placeholder.generate("implements " + afterImplements))
                                  .orElse(""*/);/*

        return Placeholder.generate(this.beforeKeyword()) + this.type + " " + this.name() + generated;*//*
    */}
	/**/}
/**/
