#include "MapNode.h"
magma.compile.public MapNode(){this((), Maps.empty(), Maps.empty(), Maps.empty());
}
magma.compile.public MapNode(magma.option.Option<String> maybeType, magma.collect.map.Map_<String, String> strings, magma.collect.map.Map_<String, magma.compile.Node> nodes, magma.collect.map.Map_<String, magma.collect.list.List_<magma.compile.Node>> nodeLists){this.maybeType = maybeType;
        this.strings = strings;
        this.nodes = nodes;
        this.nodeLists = nodeLists;
}
magma.compile.public MapNode(String type){this((type), Maps.empty(), Maps.empty(), Maps.empty());
}
String formatEntry(int depth, String key, String value){String format = "%s%s: %s";
        String indent = "\t".repeat(depth + 1);return format.formatted(indent, key, value);
}
magma.compile.Node withString(String propertyKey, String propertyValue){return MapNode(maybeType, strings.with(propertyKey, propertyValue), nodes, nodeLists);
}
magma.option.Option<String> findString(String propertyKey){return strings.find(propertyKey);
}
magma.compile.Node withNodeList(String propertyKey, magma.collect.list.List_<magma.compile.Node> propertyValues){return MapNode(maybeType, strings, nodes, nodeLists.with(propertyKey, propertyValues));
}
magma.option.Option<magma.collect.list.List_<magma.compile.Node>> findNodeList(String propertyKey){return nodeLists.find(propertyKey);
}
String display(){return format(0);
}
String format(int depth){String typeString = maybeType.map(type -> type + " ").orElse("");

        Option<String> joinedStrings = strings.stream()
                .map(entry -> formatEntry(depth, entry.left(), "\"" + entry.right() + "\""))
                .collect(new Joiner(",\n"));

        Option<String> joinedNodes = nodes.stream()
                .map(entry -> formatEntry(depth, entry.left(), entry.right().format(depth + 1)))
                .collect(new Joiner(",\n"));

        Option<String> joinedNodeLists = nodeLists.stream()
                .map(entry -> formatEntry(depth, entry.left(), formatList(entry, depth)))
                .collect(new Joiner(",\n"));

        String joined = Streams.of(joinedStrings, joinedNodes, joinedNodeLists)
                .flatMap(Streams::fromOption)
                .collect(new Joiner(",\n"))
                .orElse("");return typeString+"{\n" + joined + "\n" +
                "\t".repeat(depth) +
                "}";
}
String formatList(magma.option.Tuple<String, magma.collect.list.List_<magma.compile.Node>> entry, int depth){return "[" + entry.right()
                .stream()
                .map(node -> node.format(depth + 1))
                .collect(new Joiner(", "))
                .orElse("") + "]";
}
magma.compile.Node mapNodeList(String propertyKey, magma.collect.list.List_<magma.compile.Node>(*mapper)(magma.collect.list.List_<magma.compile.Node>)){return findNodeList(propertyKey).map(mapper).map(__lambda0__(propertyKey, nodeList)).orElse(this);
}
magma.compile.boolean is(String type){return this.maybeType.filter(__lambda1__.equals(type)).isPresent();
}
magma.compile.Node retype(String type){return MapNode((type), strings, nodes, nodeLists);
}
magma.compile.Node merge(magma.compile.Node other){Node withStrings = other.streamStrings().<Node>foldWithInitial(this, (node, tuple) -> node.withString(tuple.left(), tuple.right()));
        Node withNodes = other.streamNodes().foldWithInitial(withStrings, (node, tuple) -> node.withNode(tuple.left(), tuple.right()));return other.streamNodeLists().foldWithInitial(withNodes, __lambda2__.withNodeList(tuple.left(), tuple.right()));
}
magma.collect.stream.Stream<magma.option.Tuple<String, String>> streamStrings(){return strings.stream();
}
magma.collect.stream.Stream<magma.option.Tuple<String, magma.collect.list.List_<magma.compile.Node>>> streamNodeLists(){return nodeLists.stream();
}
magma.compile.Node withNode(String propertyKey, magma.compile.Node propertyValue){return MapNode(maybeType, strings, nodes.with(propertyKey, propertyValue), nodeLists);
}
magma.option.Option<magma.compile.Node> findNode(String propertyKey){return nodes.find(propertyKey);
}
magma.collect.stream.Stream<magma.option.Tuple<String, magma.compile.Node>> streamNodes(){return nodes.stream();
}
magma.compile.Node mapNode(String propertyKey, magma.compile.Node(*mapper)(magma.compile.Node)){return findNode(propertyKey).map(mapper).map(__lambda3__(propertyKey, node)).orElse(this);
}
magma.compile.Node withNodeLists(magma.collect.map.Map_<String, magma.collect.list.List_<magma.compile.Node>> nodeLists){return MapNode(maybeType, strings, nodes, this.nodeLists.withAll(nodeLists));
}
magma.compile.Node withNodes(magma.collect.map.Map_<String, magma.compile.Node> nodes){return MapNode(maybeType, strings, this.nodes.withAll(nodes), this.nodeLists);
}
magma.compile.Node removeNode(String propertyKey){return MapNode(maybeType, strings, nodes.remove(propertyKey), nodeLists);
}
magma.compile.boolean hasNode(String propertyKey){return nodes.containsKey(propertyKey);
}
magma.compile.boolean hasNodeList(String propertyKey){return nodeLists.containsKey(propertyKey);
}
auto __lambda0__();
auto __lambda1__();
auto __lambda2__();
auto __lambda3__();

