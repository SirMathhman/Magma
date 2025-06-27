


class Definition {
	/*@Override
    public String generateWithAfterName*/(final String afterName) {/*
        final String joinedTypeParams;*/
		if (this.typeParams.isEmpty()){
			joinedTypeParams = "";
		}
		else {
			joinedTypeParams = " < " + String.join(", ", this.typeParams) + " > ";
		}
		final var joinedModifiers = this.getString();
		return joinedModifiers + this.name + joinedTypeParams + afterName + " : " + this.type;
	}
	/*private String getString*/() {
		if (this.modifiers.isEmpty())
			return "";
		return this.modifiers.stream().map(value => value + " ").collect(Collectors.joining());
	}
	/*@Override
    public String generate*/() {
		return this.generateWithAfterName("");
	}
	/*public Definition withModifier*/(final String modifier) {
		return new Definition(this.annotations, this.modifiers.add(modifier), this.name, this.type, this.typeParams);
	}
}

