package com.magma;

record TokenInfo(int openBracePos, int semicolonPos) {
	boolean isEndOfInput() {
		return openBracePos == -1 && semicolonPos == -1;
	}

	boolean isSemicolonNext() {
		return semicolonPos != -1 && (openBracePos == -1 || semicolonPos < openBracePos);
	}
}
