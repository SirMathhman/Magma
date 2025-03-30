#include "MutableDividingState.h"
// expand List__Character = List_<struct Character>
// expand List__String = List_<struct String>
// expand List__Character = List_<struct Character>
// expand List__Character = List_<struct Character>
// expand List__String = List_<struct String>
// expand List__String = List_<struct String>
// expand Option_Tuple_Character_DividingState = Option<struct Tuple_Character_DividingState>
// expand Tuple_Character_DividingState = Tuple<struct Character, struct DividingState>
// expand Option_DividingState = Option<struct DividingState>
struct public MutableDividingState(struct List__Character queue){this(queue, Lists.empty(), new StringBuilder(), 0);}struct public MutableDividingState(struct List__Character queue, struct List__String segments, struct StringBuilder buffer, struct int depth){this.queue = queue;
        this.segments = segments;
        this.buffer = buffer;
        this.depth = depth;}struct DividingState append(struct char c){buffer.append(c);return this;}int isLevel(){return depth == 0;}struct DividingState exit(){this.depth = depth - 1;return this;}struct DividingState enter(){this.depth = depth + 1;return this;}struct DividingState advance(){this.segments = segments.add(buffer.toString());
        this.buffer = new StringBuilder();return this;}struct List__String segments(){return segments;}int isShallow(){return depth == 1;}struct Option_Tuple_Character_DividingState append(){return queue.popFirst().map(tuple -> {
            return new Tuple<>(tuple.left(), new MutableDividingState(tuple.right(), segments, buffer, depth));
        });}struct Option_DividingState appendAndDiscard(){return append().map(Tuple::right);}