struct IdentifierFilter implements Filter{};
boolean test_IdentifierFilter implements Filter(char* input) {/*
		for (int i = 0; i < input.length(); i++) {if (!Character.isLetterOrDigit(input.charAt(i))) return false;}

		return true;
	*/}
char* createErrorMessage_IdentifierFilter implements Filter() {/*
		return "Not an identifier";
	*/}
