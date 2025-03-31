#include "InvocationStartLocator.h"
magma.collect.list.List_<magma.option.Tuple<int, char>> skipDoubleQuotes(magma.collect.list.List_<magma.option.Tuple<int, char>> queue){List_<Tuple<Integer, Character>> current = queue;while (true) {
            Tuple<Boolean, Tuple<Tuple<Integer, Character>, List_<Tuple<Integer, Character>>>> first = current.popFirst()
                    .toTuple(new Tuple<>(DEFAULT_PAIR, current));

            if (first.left()) {
                Tuple<Integer, Character> pair = first.right().left();
                current = first.right().right();

                Tuple<Integer, Character> tuple = current.findFirst().orElse(DEFAULT_PAIR);
                if (tuple.right() == '\\') {
                    current = current.popFirst().orElse(new Tuple<>(DEFAULT_PAIR, current)).right();
                } else if (pair.right() == '"') break;
            } else {
                break;
            }
        }return current;
}
magma.option.Option<int> locate(String input, String infix){List_<Tuple<Integer, Character>> queue = new HeadedStream<>(new RangeHead(input.length()))
                .map(index -> input.length() - index - 1)
                .map(index -> new Tuple<>(index, input.charAt(index)))
                .collect(new ListCollector<>());

        int depth = 0;while (true) {
            Tuple<Boolean, Tuple<Tuple<Integer, Character>, List_<Tuple<Integer, Character>>>> maybeNext = queue.popFirst()
                    .toTuple(new Tuple<>(DEFAULT_PAIR, queue));

            if (maybeNext.left()) {
                Tuple<Integer, Character> pair = maybeNext.right().left();
                queue = maybeNext.right().right();

                Integer i = pair.left();
                Character c = pair.right();
                if (c == '"') {
                    queue = skipDoubleQuotes(queue);
                }

                if (c == '(' && depth == 0) return new Some<>(i);
                if (c == ')') depth++;
                if (c == '(') depth--;
            } else {
                break;
            }
        }return ();
}

