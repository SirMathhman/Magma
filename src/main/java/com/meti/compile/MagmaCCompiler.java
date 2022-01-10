package com.meti.compile;

import com.meti.compile.clang.CFormat;
import com.meti.compile.clang.CRenderer;
import com.meti.compile.common.block.Splitter;
import com.meti.compile.magma.MagmaLexer;
import com.meti.compile.node.Node;
import com.meti.compile.node.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public record MagmaCCompiler(String input) {
    public Output<String> compile() throws CompileException {
        if (input.isBlank()) return new EmptyOutput<>();

        var root = new Text(this.input);
        var input = new Splitter(root).split().collect(Collectors.toList());
        var nodes = new ArrayList<Node>();
        for (Text oldLine : input) {
            nodes.add(new MagmaLexer(oldLine).lex());
        }

        var map = new HashMap<CFormat, List<Node>>();
        for (Node node : nodes) {
            if (node.is(Node.Type.Structure)) {
                List<Node> list = new ArrayList<>();
                if(!map.containsKey(CFormat.Header)) {
                    map.put(CFormat.Header, list);
                } else {
                    list = map.get(CFormat.Header);
                }
                list.add(node);
            } else {
                List<Node> list = new ArrayList<>();
                if(!map.containsKey(CFormat.Source)) {
                    map.put(CFormat.Source, list);
                } else {
                    list = map.get(CFormat.Source);
                }
                list.add(node);
            }
        }

        var output = new MappedOutput<>(map);
        return output.map((format, list) -> {
            var builder = new StringBuilder();
            for (Node line : list) {
                builder.append(new CRenderer(line).render().compute());
            }
            return builder.toString();
        });
    }
}
