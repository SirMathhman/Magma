#include "TreeTransformingStage.h"
struct public TreeTransformingStage(struct Transformer transformer}{this.transformer = transformer;}Result<struct Node, struct CompileError> transform(struct Node root, struct State state}{return transformer.beforePass(state, root).flatMapValue(beforePass -> beforePass.streamNodes()
                .foldToResult(root, (node, tuple) -> mapNodes(node, tuple, state))
                .flatMapValue(withNodes -> transformNodeLists(withNodes, state)));}Result<struct Node, struct CompileError> transformNodeLists(struct Node withNodes, struct State state}{return withNodes.streamNodeLists()
                .foldToResult(withNodes, (node, tuple) -> mapNodeList(node, tuple, state))
                .flatMapValue(node1 -> transformer.afterPass(state, node1));}Result<struct Node, struct CompileError> mapNodes(struct Node node, Tuple<struct String, struct Node> tuple, struct State state}{return transform(tuple.right(), state).mapValue(newChild -> node.withNode(tuple.left(), newChild));}Result<struct Node, struct CompileError> mapNodeList(struct Node node, Tuple<struct String, List_<struct Node>> tuple, struct State state}{return tuple.right()
                .stream()
                .foldToResult(Lists.<Node>empty(), (current, element) -> mapNodeListElement(current, element, state))
                .mapValue(children -> node.withNodeList(tuple.left(), children));}Result<List_<struct Node>, struct CompileError> mapNodeListElement(List_<struct Node> elements, struct Node element, struct State state}{return transform(element, state).mapValue(elements::add);}