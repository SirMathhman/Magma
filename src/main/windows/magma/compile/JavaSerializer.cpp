// Generated transpiled C++ from 'src\main\java\magma\compile\JavaSerializer.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct JavaSerializer {};
Result<> deserialize_JavaSerializer(Class<> clazz, Node node) {
	if (Objects.isNull(clazz))return new_Err_((new_CompileError(("", new_NodeContext((node)));
	if (Objects.isNull(node))return new_Err_((new_CompileError(("", new_StringContext((clazz.getName())));
	return deserializeValue((clazz, node).mapValue(clazz::cast);
}
Result<> serialize_JavaSerializer(Class<> clazz, T value) {
	if (Objects.isNull(clazz))return new_Err_((new_CompileError(("", new_StringContext(("")));
	if (Objects.isNull(value))return new_Err_((new_CompileError((""+clazz.getName()+"", new_StringContext(("")));
	return serializeValue((clazz, value);
}
private static Result<> serializeValue_JavaSerializer(Class<> type, Object value) {
	if (type.isSealed()&&/*???*/.isRecord())return serializeSealed((type, value);
	if (/*???*/.isRecord())return new_Err_((new_CompileError((""+type.getName()+"", new_StringContext((type.getName())));
	return serializeRecord((type, value);
}
private static Result<> serializeSealed_JavaSerializer(Class<> type, Object value) {
	final Class<> concreteClass=value.getClass();
	if (/*???*/.isAssignableFrom(concreteClass))return new_Err_((new_CompileError((""+concreteClass.getName()+""+type.getName()+"", new_StringContext((concreteClass.getName())));
	return serializeValue((concreteClass, value);
}
private static Result<> serializeRecord_JavaSerializer(Class<> type, Object value) {
	Node result=createNodeWithType((type);
	new ArrayList<>();
	RecordComponent* recordComponents=type.getRecordComponents();
	int i=0;
	while (/*???*/)
	{
	RecordComponent component=/*???*/;/*???*//*???*/
	i++;}
	new Ok<>(result);
	return new_Err_((new_CompileError((""+type.getSimpleName()+"", new_StringContext((type.getName()), errors));
}
private static Result<> serializeField_JavaSerializer(RecordComponent component, Object value) {
	char* fieldName=component.getName();
	Class<> fieldType=component.getType();
	if (Objects.isNull(value))return new_Err_((new_CompileError((""+fieldName+"", new_StringContext((fieldName)));
	if (fieldType==String.class)return new_Ok_((new_Node(().withString(fieldName, (String) value));
	if (Option.class.isAssignableFrom(fieldType))return serializeOptionField((component, value);
	if (List.class.isAssignableFrom(fieldType))return serializeListField((component, value);
	return serializeValue((fieldType, value).mapValue(childNode -> new Node().withNode(fieldName, childNode));
}
private static Result<> serializeOptionField_JavaSerializer(RecordComponent component, Object value) {
	char* fieldName=component.getName();
	if (/*???*/)return new_Err_((new_CompileError((""+fieldName+"", new_StringContext((fieldName)));
	if (/*???*/)return new_Ok_((new_Node(());
	if (/*???*/)
	{
	Result<> elementTypeResult=getGenericArgument((component.getGenericType());
	new Err<>(error);
	Type elementType=/*???*/.value();
	Result<> elementClassResult=erase((elementType);
	new Err<>(error);
	Class<> elementClass=/*???*/.value();
	if (elementClass==String.class)return new_Ok_((new_Node(().withString(fieldName, (String) value1));
	if (List.class.isAssignableFrom(elementClass))return serializeOptionListField((fieldName, elementType, value1);
	return serializeValue((elementClass, value1).mapValue(childNode -> new Node().withNode(fieldName, childNode));}
	return new_Ok_((new_Node(());
}
private static Result<> serializeOptionListField_JavaSerializer(char* fieldName, Type listType, Object content) {
	if (/*???*/)return new_Err_((new_CompileError((""+fieldName+"", new_StringContext((fieldName)));
	Result<> elementTypeResult=getGenericArgument((listType);
	new Err<>(error);
	Type elementType=/*???*/.value();
	Result<> elementClassResult=erase((elementType);
	new Err<>(error);
	Class<> elementClass=/*???*/.value();
	return serializeListElements((elementClass, list).mapValue(nodes -> {
			if (nodes.isEmpty()) return new Node();
			return new Node().withNodeList(fieldName, nodes);
		});
}
private static Result<> serializeListField_JavaSerializer(RecordComponent component, Object value) {
	char* fieldName=component.getName();
	if (/*???*/)return new_Err_((new_CompileError((""+fieldName+"", new_StringContext((fieldName)));
	Result<> elementTypeResult=getGenericArgument((component.getGenericType());
	new Err<>(error);
	Type elementType=/*???*/.value();
	Result<> elementClassResult=erase((elementType);
	new Err<>(error);
	Class<> elementClass=/*???*/.value();
	return serializeListElements((elementClass, list).mapValue(nodes -> {
			if (nodes.isEmpty()) return new Node();
			return new Node().withNodeList(fieldName, nodes);
		});
}
private static Result<> serializeListElements_JavaSerializer(Class<> elementClass, List<> list) {
	new ArrayList<>();
	new ArrayList<>();
	list.stream(().map((element -> serializeValue(elementClass, element)).forEach((/*???*/);
	new Ok<>(nodes);
	return new_Err_((new_CompileError(("", new_StringContext((""), errors));
}
private static Result<> deserializeValue_JavaSerializer(Class<> type, Node node) {
	if (type.isSealed()&&/*???*/.isRecord())return deserializeSealed((type, node);
	if (/*???*/.isRecord())return new_Err_((new_CompileError((""+type.getName()+"", new_NodeContext((node)));
	return deserializeRecord((type, node);
}
private static Result<> deserializeSealed_JavaSerializer(Class<> type, Node node) {
	if (/*???*/)return new_Err_((new_CompileError((""+type.getName()+"", new_NodeContext((node)));
	Option<> directResult=tryDirectPermittedSubclasses((type, node, nodeType);
	return result;
	return tryNestedSealedInterfaces((type, node, nodeType);
}
private static Option<> tryDirectPermittedSubclasses_JavaSerializer(Class<> type, Node node, char* nodeType) {
	Class<>* permittedSubclasses=type.getPermittedSubclasses();
	int i=0;
	while (/*???*/)
	{
	Class<> permitted=/*???*/;
	Option<> maybeIdentifier=resolveTypeIdentifier((permitted);
	if (/*???*/&&identifier.equals(nodeType))return new_Some_((deserializeValue((permitted, node));
	i++;}
	new None<>();
}
private static Result<> tryNestedSealedInterfaces_JavaSerializer(Class<> type, Node node, char* nodeType) {
	Option<> recursiveResult=findNestedSealedDeserialization((type, node, nodeType);
	return value;
	List<> validTags=collectAllValidTags((type);
	char* validTagsList;
	if (validTags.isEmpty())validTagsList="";
	else validTagsList=String.join(", ", validTags);
	char* suggestion=getSuggestionForUnknownTag((type, nodeType, validTags);
	return new_Err_((new_CompileError((""+type.getSimpleName()+""+nodeType+""+""+validTagsList+""+suggestion, new_NodeContext((node)));
}
private static Option<> findNestedSealedDeserialization_JavaSerializer(Class<> type, Node node, char* nodeType) {
	Class<>* subclasses=type.getPermittedSubclasses();
	int j=0;
	while (/*???*/)
	{
	Class<> permitted=/*???*/;
	Option<> recursiveResult=tryDeserializeNestedSealed((type, node, nodeType, permitted);
	return k;
	j++;}
	new None<>();
}
private static Option<> tryDeserializeNestedSealed_JavaSerializer(Class<> type, Node node, char* nodeType, Class<> permitted) {
	new None<>();
	Result<> recursiveResult=deserializeSealed((permitted, node);
	new Some<>(recursiveResult);
	new Some<>(recursiveResult);
	new None<>();
}
char* getSuggestionForUnknownTag_JavaSerializer(Class<> type, char* nodeType, List<> validTags) {
	if (validTags.isEmpty())return "";
	Option<> closestTag=findClosestTag((nodeType, validTags);
	if (/*???*/)return ""+tag+""+nodeType+""+type.getSimpleName()+"";
	return ""+nodeType+""+type.getSimpleName()+"";
}
private static Option<> findClosestTag_JavaSerializer(char* nodeType, List<> validTags) {
	Option<> closest=Option.empty();
	int minDistance=Integer.MAX_VALUE;
	int i=0;
	while (/*???*/)
	{
	char* tag=validTags.get(i);
	int distance=levenshteinDistance((nodeType.toLowerCase(), tag.toLowerCase());
	if (/*???*/)minDistance=distance;
	closest=Option.of(tag);
	i++;}
	return closest;
}
int levenshteinDistance_JavaSerializer(char* s1, char* s2) {
	int** dp=/*???*/;
	int bound=s1.length();
	int i1=0;
	while (/*???*/)
	{
	/*???*/=i1;
	i1++;}
	int bound1=s2.length();
	int j=0;
	while (/*???*/)
	{
	/*???*/=j;
	j++;}
	int i=1;
	while (/*???*/)
	{
	fillLevenshteinRow((dp, s1, s2, i);
	i++;}
	return dp[s1.length()][s2.length()];
}
void fillLevenshteinRow_JavaSerializer(int** dp, char* s1, char* s2, int i) {
	int j=1;
	while (/*???*/)
	{
	if (s1.charAt(i - 1)==s2.charAt(j - 1))/*???*/=/*???*/;
	else dp[i][j]=1+Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
	j++;}
}
private static List<> collectAllValidTags_JavaSerializer(Class<> sealedType) {
	new ArrayList<>();
	Arrays.stream((sealedType.getPermittedSubclasses()).forEach((/*???*/);
	return tags;
}
boolean canMatchType_JavaSerializer(Class<> sealedType, char* nodeType) {
	Class<>* permittedSubclasses=sealedType.getPermittedSubclasses();
	int i=0;
	while (/*???*/)
	{
	Class<> permitted=/*???*/;
	Option<> maybeIdentifier=resolveTypeIdentifier((permitted);
	return true;
	return true;
	i++;}
	return false;
}
private static Result<> deserializeRecord_JavaSerializer(Class<> type, Node node) {
	Option<> expectedType=resolveTypeIdentifier((type);
	if (/*???*/)
	if (/*???*/)
	{
	if (/*???*/.is(expectedType0))return new_Err_((new_CompileError((""+expectedType0+""+nodeType+"", new_NodeContext((node)));}
	else
	return new_Err_((new_CompileError((""+type.getSimpleName()+""+expectedType0+"", new_NodeContext((node)));
	RecordComponent* components=type.getRecordComponents();
	new Object[components.length];
	new ArrayList<>();
	new HashSet<>();
	IntStream.range((0, components.length).forEach((/*???*/);
	Option<> validationError=validateAllFieldsConsumed((node, consumedFields, type);
	if (/*???*/)errors.add((error);
	if (/*???*/.isEmpty())return new_Err_((new_CompileError((""+type.getSimpleName()+"", new_NodeContext((node), errors));/*???*//*???*/
}
private static Result<> deserializeField_JavaSerializer(RecordComponent component, Node node, Set<> consumedFields) {
	char* fieldName=component.getName();
	Class<> fieldType=component.getType();
	if (fieldType==String.class)return deserializeStringField((fieldName, node, consumedFields);
	if (Option.class.isAssignableFrom(fieldType))return deserializeOptionField((component, node, consumedFields);
	if (List.class.isAssignableFrom(fieldType))return deserializeListField((component, node, consumedFields);
	Option<> childNode=node.findNode(fieldName);
	if (/*???*/)
	{
	consumedFields.add((fieldName);
	return deserializeValue((fieldType, value);}
	else
	return new_Err_((new_CompileError((""+fieldName+""+fieldType.getSimpleName()+"", new_NodeContext((node)));
}
private static Result<> deserializeStringField_JavaSerializer(char* fieldName, Node node, Set<> consumedFields) {
	Option<> direct=node.findString(fieldName);
	if (/*???*/)
	{
	consumedFields.add((fieldName);
	new Ok<>(value);}
	Option<> nested=findStringInChildren((node, fieldName);
	if (/*???*/)
	{
	consumedFields.add((fieldName);
	new Ok<>(value);}
	else
	return new_Err_((new_CompileError((""+fieldName+"", new_NodeContext((node)));
}
private static Result<> deserializeOptionField_JavaSerializer(RecordComponent component, Node node, Set<> consumedFields) {
	Result<> elementTypeResult=getGenericArgument((component.getGenericType());
	new Err<>(error);
	Type elementType=/*???*/.value();
	Result<> elementClassResult=erase((elementType);
	new Err<>(error);
	Class<> elementClass=/*???*/.value();
	char* fieldName=component.getName();
	if (elementClass==String.class)
	{
	Option<> direct=node.findString(fieldName);
	if (/*???*/)
	{
	consumedFields.add((fieldName);
	new Ok<>(direct);}
	Option<> nested=findStringInChildren((node, fieldName);
	if (/*???*/)
	{
	consumedFields.add((fieldName);
	new Ok<>(nested);}
	Option<> wrongTypeNode=node.findNode(fieldName);
	if (/*???*/)return new_Err_((new_CompileError((""+fieldName+""+node.maybeType.orElse("unknown")+"", new_NodeContext((node)));
	Option<> wrongTypeList=node.findNodeList(fieldName);
	if (/*???*/)return new_Err_((new_CompileError((""+fieldName+""+node.maybeType.orElse("unknown")+"", new_NodeContext((node)));
	new Ok<>(Option.empty());}
	if (List.class.isAssignableFrom(elementClass))return deserializeOptionListField((fieldName, elementType, node, consumedFields);
	Option<> childNode=node.findNode(fieldName);
	if (/*???*/)
	{
	consumedFields.add((fieldName);
	return deserializeValue((elementClass, value).mapValue(Option::of);}
	new Ok<>(Option.empty());
}
private static Result<> deserializeOptionListField_JavaSerializer(char* fieldName, Type listType, Node node, Set<> consumedFields) {
	Result<> elementTypeResult=getGenericArgument((listType);
	new Err<>(error);
	Type elementType=/*???*/.value();
	Result<> elementClassResult=erase((elementType);
	new Err<>(error);
	Class<> elementClass=/*???*/.value();
	Option<> maybeList=node.findNodeList(fieldName);
	if (/*???*/)
	{
	consumedFields.add((fieldName);
	Result<> elementsResult=deserializeListElements((elementClass, value);
	return elementsResult.mapValue(list -> Option.of(List.copyOf(list)));}
	new Ok<>(Option.empty());
}
private static Result<> deserializeListField_JavaSerializer(RecordComponent component, Node node, Set<> consumedFields) {
	char* fieldName=component.getName();
	Result<> elementTypeResult=getGenericArgument((component.getGenericType());
	new Err<>(error);
	Type elementType=/*???*/.value();
	Result<> elementClassResult=erase((elementType);
	new Err<>(error);
	Class<> elementClass=/*???*/.value();
	Option<> maybeList=node.findNodeList(fieldName);
	if (/*???*/)
	{
	consumedFields.add((fieldName);
	Result<> elementsResult=deserializeListElements((elementClass, value);
	return elementsResult.mapValue(List::copyOf);}
	else
	return new_Err_((new_CompileError((""+fieldName+"", new_NodeContext((node)));
}
private static Result<> deserializeListElements_JavaSerializer(Class<> elementClass, List<> nodeList) {
	new ArrayList<>();
	new ArrayList<>();
	int index=0;
	int i=0;
	while (/*???*/)
	{
	Node childNode=nodeList.get(i);
	Result<> childResult=deserializeValue((elementClass, childNode);
	if (/*???*/)results.add((value);
	else
	if (/*???*/)
	if (elementClass.isSealed()&&/*???*/)
	{
	CompileError wrappedError=new_CompileError((""+index+""+nodeType+""+elementClass.getSimpleName()+"", new_NodeContext((childNode), List.of(error));
	errors.add((wrappedError);}
	else
	if (shouldBeDeserializableAs((childNode, elementClass))errors.add((error);
	index++;
	i++;}
	new Ok<>(results);
	return new_Err_((new_CompileError((""+errors.size()+""+nodeList.size()+""+elementClass.getSimpleName()+"", new_NodeContext((nodeList.getFirst()), errors));
}
Node createNodeWithType_JavaSerializer(Class<> type) {
	new Node();
	Option<> typeId=resolveTypeIdentifier((type);
	if (/*???*/)node.retype((value);
	return node;
}
Node mergeNodes_JavaSerializer(Node base, Node addition) {
	new Node();
	result.maybeType=base.maybeType;
	result.merge((base);
	result.merge((addition);
	return result;
}
private static Result<> getGenericArgument_JavaSerializer(Type type) {
	if (/*???*/)
	{
	Type* args=parameterized.getActualTypeArguments();
	if (args.length > 0)new Ok<>(args[0]);}
	return new_Err_((new_CompileError((""+type+"", new_StringContext((type.toString())));
}
private static Result<> erase_JavaSerializer(Type type) {
	new Ok<>(clazz);
	new Ok<>(raw);
	return new_Err_((new_CompileError((""+type+"", new_StringContext((type.toString())));
}
private static Option<> resolveTypeIdentifier_JavaSerializer(Class<> clazz) {
	Tag annotation=clazz.getAnnotation(Tag.class);
	return Option.empty();
	return Option.of(annotation.value());
}
private static Option<> findStringInChildren_JavaSerializer(Node node, char* key) {
	{
	Iterator<> iterator=node.nodes.values().iterator();
	while (iterator.hasNext())
	{
	Node child=iterator.next();
	Option<> result=child.findString(key);
	return result;
	result=findStringInChildren((child, key);
	return result;}}
	return findStringInNodeLists((node, key);
}
private static Option<> findStringInNodeLists_JavaSerializer(Node node, char* key) {
	Iterator<> iterator=node.nodeLists.values().iterator();
	while (iterator.hasNext())
	{
	List<> children=iterator.next();
	Option<> result=searchChildrenList((children, key);
	return result;}
	return Option.empty();
}
private static Option<> searchChildrenList_JavaSerializer(List<> children, char* key) {
	int i=0;
	while (/*???*/)
	{
	Node child=children.get(i);
	Option<> result=child.findString(key);
	return result;
	result=findStringInChildren((child, key);
	return result;
	i++;}
	return Option.empty();
}
boolean shouldBeDeserializableAs_JavaSerializer(Node node, Class<> targetClass) {
	return false;
	if (/*???*/)
	{
	Tag tagAnnotation=targetClass.getAnnotation(Tag.class);
	return nodeType.equals(tagAnnotation.value());
	char* targetName=targetClass.getSimpleName().toLowerCase();
	return nodeType.toLowerCase().contains(targetName) || targetName.contains(nodeType.toLowerCase());}
	return false;
}
private static Option<> validateAllFieldsConsumed_JavaSerializer(Node node, Set<> consumedFields, Class<> targetClass) {
	new HashSet<>();
	allFields.addAll((getStringKeys((node));
	allFields.addAll((node.nodes.keySet());
	allFields.addAll((node.nodeLists.keySet());
	new HashSet<>(allFields);
	leftoverFields.removeAll((consumedFields);
	if (/*???*/.isEmpty())
	{
	char* leftoverList=String.join(", ", leftoverFields);
	return Option.of(new CompileError(
					"Incomplete deserialization for '"+targetClass.getSimpleName() + "': leftover fields [" + leftoverList +
					"] were not consumed." + "This indicates a mismatch between the Node structure and the target ADT.",
					new NodeContext(node)));}
	return Option.empty();
}
private static Set<> getStringKeys_JavaSerializer(Node node) {
	return node.getStringKeys();
}
