


class HeadedStream<Value> {
	HeadedStream() : public {
	}
	map<Some[value=]>() : Stream<Some[value=]> {
	}
	collect<Some[value=]>() : C {
	}
	fold<Some[value=]>(, folder : BiFunction<Some[value=, Value, Return]>) : Return {/*
        while (true) {
            final Return finalCurrent = current;
            final var tuple = this.head.next().map(next -> folder.apply(finalCurrent, next)).toTuple(current);
            if (tuple.left())
                current = tuple.right();
            else
                break;
        }*/
		return current;
	}
	flatMap<Some[value=]>() : Stream<Some[value=]> {
	}
	filter() : Stream<Some[value=]> {
	}
	next() : Optional<Some[value=]> {
	}
}

