#include "InfixRule.h"
int __lambda0__(){return right;
}
int __lambda1__(){return tuple;
}
public InfixRule(Rule left, String infix, Rule right, Locator locator){this.left = left;
        this.infix = infix;
        this.right = right;
        this.locator = locator;
}
Result<Node, CompileError> parse(String input){return locator.locate(input, infix).map(index -> {
            String left = input.substring(0, index);
            String right = input.substring(index + infix.length());
            return this.left.parse(left).and(() -> this.right.parse(right)).mapValue(tuple -> tuple.left().merge(tuple.right()));
        }).orElseGet(() -> {
            return new Err<>(new CompileError("Infix '" + infix + "' not present", new StringContext(input)));
        });
}
Result<String, CompileError> generate(Node node){return left.generate(node).and(__lambda0__.generate(node)).mapValue(__lambda1__.left()+infix+tuple.right());
}
