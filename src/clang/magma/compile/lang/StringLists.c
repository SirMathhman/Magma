#include "StringLists.h"
auto __lambda0__(){return segment;
}
magma.compile.Node toQualified(magma.collect.list.List_<String> list){List_<Node> resolved = list.stream()
                .map(segment -> new MapNode().withString("value", segment))
                .collect(new ListCollector<Node>());return MapNode("qualified").withNodeList("segments", resolved);
}
magma.collect.list.List_<String> fromQualifiedType(magma.compile.Node node){return node.findNodeList("segments").orElse(Lists.empty()).stream().map(__lambda0__.findString("value")).flatMap(Streams.fromOption).collect(());
}
