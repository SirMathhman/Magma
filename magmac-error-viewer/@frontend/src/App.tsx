import axios from "axios";
import {createMemo, createSignal, JSX, onMount, Show} from "solid-js";

function unescape(value: string): string {
    return value.replace(/&quot;/g, "\"")
        .replace(/&lt;/g, "<")
        .replace(/&gt;/g, ">")
        .replace(/&apos;/g, "'")
        .replace(/&amp;/g, "&")
        .replace(/\\\\/g, "\\")
        .replace(/\\n/g, "\n")
        .replace(/\\t/g, "\t")
        .replace(/\\r/g, "\r");
}

interface TreeContent {
    context?: string;
    title: string;
}

function TreeElement({content, children, onClick}: {
    content: TreeContent,
    children: JSX.Element,
    onClick: (content: TreeContent) => void;
}) {
    const [expanded, setExpanded] = createSignal(false);

    const expandedText = createMemo(() => {
        return expanded() ? "^" : "V";
    });

    function toggleExpanded() {
        setExpanded(value => !value);
    }

    return (
        <div>
            <div style={{
                display: "flex",
                "flex-direction": "row",
                gap: "0.5rem"
            }}>
                <button onClick={toggleExpanded}>
                    {expandedText()}
                </button>
                <span onClick={() => {
                    onClick(content);
                }}>
                {content.title}
            </span>
            </div>
            <Show when={expanded()}>
                <div style={{"border-left": "1px solid black", "padding-left": "1rem"}}>
                    {children}
                </div>
            </Show>
        </div>
    );
}

function createTreeElement(tree: XMLObject | undefined, onClick: (content: TreeContent) => void) {
    if (!tree) return <></>;

    const parents = tree.findChildren("parent").map(parent => createTreeElement(parent, onClick));
    const collections = tree.findChildren("collection").map(collection => createTreeElement(collection, onClick));
    const children = tree.findChildren("child").map(child => createTreeElement(child, onClick));

    const message = tree.findAttribute("message") ?? "";
    const context = tree.findAttribute("context") ?? tree.findContent()

    const content: TreeContent = {
        context: context ? unescape(context) : undefined,
        title: unescape(message)
    }

    return <TreeElement content={content} onClick={onClick}>
        {parents}
        {collections}
        {children}
    </TreeElement>
}

interface XMLObject {
    findAttribute(key: string): string | undefined;

    findChildren(key: string): XMLObject[];

    findContent(): string | undefined;
}

function XMLObject(tag: string, obj: any): XMLObject {
    return {
        findAttribute(propertyKey: string): string | undefined {
            const properties = obj.$;
            if (!properties) return undefined;
            return properties[propertyKey];
        }, findChildren(key: string): XMLObject[] {
            const children = obj[key] ?? [];
            return children.map((child: any) => XMLObject(tag, child));
        },
        findContent(): string | undefined {
            return obj["_"];
        }
    }
}

function App() {
    const [tree, setTree] = createSignal<XMLObject | undefined>(undefined);

    const [content, setContent] = createSignal("");
    const [before, setBefore] = createSignal("");
    const [highlighted, setHighlighted] = createSignal("");
    const [after, setAfter] = createSignal("");
    const [path, setPath] = createSignal("");

    function update() {
        const data = {
            path: path()
        };

        axios({
            method: "post",
            url: "http://localhost:3000/tree",
            data: data
        }).then(e => {
            const root = XMLObject("parent", e.data.parent);
            setTree(root);
        }).catch(e => {
            console.error(e);
        });

        axios({
            method: "post",
            url: "http://localhost:3000/content",
            data: data
        }).then(e => {
            const unescaped = unescape(e.data);
            setContent(unescaped);
            setBefore(unescaped);
        }).catch(e => {
            console.error(e);
        });
    }

    onMount(() => {
        update();
    });

    function onClick(clicked: TreeContent) {
        console.log(clicked);
        const context = clicked.context;
        if (!context) return;

        const currentContent = content();
        const index = currentContent.indexOf(context);
        if (index == -1) {
            console.error("Context does not exist in content: " + context);
            return;
        }

        const beforeSlice = currentContent.slice(0, index);
        const highlightedSlice = currentContent.slice(index, index + context.length);
        const afterSlice = currentContent.slice(index + context.length);

        setBefore(beforeSlice);
        setHighlighted(highlightedSlice);
        setAfter(afterSlice);
    }

    function updateImpl() {
        update();
        setPath("");
    }

    return (
        <div style={{
            display: "flex",
            "flex-direction": "row",
            "justify-content": "center",
            "align-items": "center",
            width: "100%",
            height: "100%"
        }}>
            <div style={{
                width: "80vw",
                height: "80vh"
            }}>
                <div style={{
                    display: "flex",
                    "flex-direction": "column"
                }}>
                    <div>
                        <span>
                            Path
                        </span>
                        <input value={path()} onChange={e => setPath(e.target.value)}/>
                        <button onClick={() => updateImpl()}>
                            Submit
                        </button>
                    </div>
                    <hr/>
                    <div style={{
                        display: "flex",
                        "flex-direction": "row",
                        width: "100%",
                        height: "100%"
                    }}>
                        <div style={{
                            width: "50%",
                            height: "100%",
                        }}>
                        <span>
                            Navigator
                        </span>
                            <div style={{
                                overflow: "scroll",
                                "white-space": "nowrap",
                                width: "100%",
                                height: "100%"
                            }}>
                                {
                                    createTreeElement(tree(), onClick)
                                }
                            </div>
                        </div>
                        <div style={{
                            width: "50%",
                            height: "100%"
                        }}>
                            <div style={{
                                padding: "1rem",
                                width: "100%",
                                height: "100%"
                            }}>
                            <span>
                                Content
                            </span>
                                <div style={{
                                    overflow: "scroll",
                                    width: "100%",
                                    height: "100%"
                                }}>
                                <pre>
                                    <span>
                                        {before()}
                                    </span>
                                    <span style={{"background-color": "red"}}>
                                        {highlighted()}
                                    </span>
                                    <span>
                                        {after()}
                                    </span>
                                </pre>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default App
