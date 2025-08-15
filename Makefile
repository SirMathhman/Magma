.PHONY: test lint check

test:
	mvn test

lint:
	mvn checkstyle:check

check: test lint