// Generated transpiled C++ from 'src\main\java\magma\compile\JavaSerializer.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct JavaSerializer {};
Result<T, CompileError> deserialize_JavaSerializer(Class<T> clazz, Node node) {
	if (Objects.isNull(clazz))return new_???(new_???("", new_???(node)));
	if (Objects.isNull(node))return new_???(new_???("", new_???(clazz.getName())));
	return deserializeValue(clazz, node).mapValue(/*???*/);
}
Result<Node, CompileError> serialize_JavaSerializer(Class<T> clazz, T value) {
	if (Objects.isNull(clazz))return new_???(new_???("", new_???("")));
	if (Objects.isNull(value))return new_???(new_???(""+clazz.getName()+"", new_???("")));
	return serializeValue(clazz, value);
}
Result<Node, CompileError> serializeValue_JavaSerializer(Class</*Wildcard[]*/> type, Object value) {
	if (type.isSealed()&&/*???*/.isRecord())return serializeSealed(type, value);
	if (/*???*/.isRecord())return new_???(new_???(""+type.getName()+"", new_???(type.getName())));
	return serializeRecord(type, value);
}
Result<Node, CompileError> serializeSealed_JavaSerializer(Class</*Wildcard[]*/> type, Object value) {
	Class</*Wildcard[]*/> concreteClass=value.getClass();
	if (/*???*/.isAssignableFrom(concreteClass))return new_???(new_???(""+concreteClass.getName()+""+type.getName()+"", new_???(concreteClass.getName())));
	return serializeValue(concreteClass, value);
}
Result<Node, CompileError> serializeRecord_JavaSerializer(Class</*Wildcard[]*/> type, Object value) {
	Node result=createNodeWithType(type);
	/*???*/=new_???();
	RecordComponent* recordComponents=type.getRecordComponents();
	int i=/*???*/;
	while (/*???*/)
	{
	RecordComponent component=/*???*/;/*???*//*???*/
	i++;}
	if (errors.isEmpty())return new_???(result);
	return new_???(new_???(""+type.getSimpleName()+"", new_???(type.getName()), errors));
}
Result<Node, CompileError> serializeField_JavaSerializer(RecordComponent component, Object value) {
	String fieldName=component.getName();
	Class</*Wildcard[]*/> fieldType=component.getType();
	if (Objects.isNull(value))return new_???(new_???(""+fieldName+"", new_???(fieldName)));
	if (fieldType==String.class)return new_???(new_???(fieldName, /*???*/));
	if (Option.class.isAssignableFrom(fieldType))return serializeOptionField(component, value);
	if (magma.list.NonEmptyList.class.isAssignableFrom(fieldType))return serializeNonEmptyListField(component, value);
	if (List.class.isAssignableFrom(fieldType))return serializeListField(component, value);
	return serializeValue(fieldType, value).mapValue(/*???*/.withNode(fieldName, childNode));
}
Result<Node, CompileError> serializeOptionField_JavaSerializer(RecordComponent component, Object value) {
	String fieldName=component.getName();
	if (/*???*/)return new_???(new_???(""+fieldName+"", new_???(fieldName)));
	if (/*???*/)return new_???(new_???());
	if (/*???*/)
	{
	Result<Type, CompileError> elementTypeResult=getGenericArgument(component.getGenericType());
	if (/*???*/)return new_???(error);
	Type elementType=/*???*/.value();
	Result<Class</*Wildcard[]*/>, CompileError> elementClassResult=erase(elementType);
	if (/*???*/)return new_???(error);
	Class</*Wildcard[]*/> elementClass=/*???*/.value();
	if (elementClass==String.class)return new_???(new_???(fieldName, /*???*/));
	if (List.class.isAssignableFrom(elementClass))return serializeOptionListField(fieldName, elementType, value1);
	return serializeValue(elementClass, value1).mapValue(/*???*/.withNode(fieldName, childNode));}
	return new_???(new_???());
}
Result<Node, CompileError> serializeOptionListField_JavaSerializer(String fieldName, Type listType, Object content) {
	if (/*???*/)return new_???(new_???(""+fieldName+"", new_???(fieldName)));
	Result<Type, CompileError> elementTypeResult=getGenericArgument(listType);
	if (/*???*/)return new_???(error);
	Type elementType=/*???*/.value();
	Result<Class</*Wildcard[]*/>, CompileError> elementClassResult=erase(elementType);
	if (/*???*/)return new_???(error);
	Class</*Wildcard[]*/> elementClass=/*???*/.value();
	return serializeListElements(elementClass, list).mapValue(/*???*/);
}
Result<Node, CompileError> serializeNonEmptyListField_JavaSerializer(RecordComponent component, Object value) {
	String fieldName=component.getName();
	if (/*???*/)return new_???(new_???(""+fieldName+"", new_???(fieldName)));
	Result<Type, CompileError> elementTypeResult=getGenericArgument(component.getGenericType());
	if (/*???*/)return new_???(error);
	Type elementType=/*???*/.value();
	Result<Class</*Wildcard[]*/>, CompileError> elementClassResult=erase(elementType);
	if (/*???*/)return new_???(error);
	Class</*Wildcard[]*/> elementClass=/*???*/.value();
	List</*Wildcard[]*/> list=nonEmptyList.toList();
	return serializeListElements(elementClass, list).mapValue(/*???*/.withNodeList(fieldName, nodes));
}
Result<Node, CompileError> serializeListField_JavaSerializer(RecordComponent component, Object value) {
	String fieldName=component.getName();
	if (/*???*/)return new_???(new_???(""+fieldName+"", new_???(fieldName)));
	Result<Type, CompileError> elementTypeResult=getGenericArgument(component.getGenericType());
	if (/*???*/)return new_???(error);
	Type elementType=/*???*/.value();
	Result<Class</*Wildcard[]*/>, CompileError> elementClassResult=erase(elementType);
	if (/*???*/)return new_???(error);
	Class</*Wildcard[]*/> elementClass=/*???*/.value();
	return serializeListElements(elementClass, list).mapValue(/*???*/);
}
Result<List<Node>, CompileError> serializeListElements_JavaSerializer(Class</*Wildcard[]*/> elementClass, List</*Wildcard[]*/> list) {
	/*???*/=new_???();
	/*???*/=new_???();
	list.stream().map(/*???*/(elementClass, element)).forEach(/*???*/);
	if (errors.isEmpty())return new_???(nodes);
	return new_???(new_???("", new_???(""), errors));
}
Result<Object, CompileError> deserializeValue_JavaSerializer(Class</*Wildcard[]*/> type, Node node) {
	if (type.isSealed()&&/*???*/.isRecord())return deserializeSealed(type, node);
	if (/*???*/.isRecord())return new_???(new_???(""+type.getName()+"", new_???(node)));
	return deserializeRecord(type, node);
}
Result<Object, CompileError> deserializeSealed_JavaSerializer(Class</*Wildcard[]*/> type, Node node) {
	if (/*???*/)return new_???(new_???(""+type.getName()+"", new_???(node)));
	Option<Result<Object, CompileError>> directResult=tryDirectPermittedSubclasses(type, node, nodeType);
	if (/*???*/)return result;
	return tryNestedSealedInterfaces(type, node, nodeType);
}
Option<Result<Object, CompileError>> tryDirectPermittedSubclasses_JavaSerializer(Class</*Wildcard[]*/> type, Node node, String nodeType) {
	Class</*Wildcard[]*/>* permittedSubclasses=type.getPermittedSubclasses();
	int i=/*???*/;
	while (/*???*/)
	{
	Class</*Wildcard[]*/> permitted=/*???*/;
	/*???*/(permitted);
	if (/*???*/&&identifier.equals(nodeType))return new_???(deserializeValue(permitted, node));
	i++;}
	return new_???();
}
Result<Object, CompileError> tryNestedSealedInterfaces_JavaSerializer(Class</*Wildcard[]*/> type, Node node, String nodeType) {
	Option<Result<Object, CompileError>> recursiveResult=findNestedSealedDeserialization(type, node, nodeType);
	if (/*???*/)return value;
	/*???*/(type);
	String validTagsList;
	if (validTags.isEmpty())validTagsList="";
	else
	validTagsList==validTags.stream().collect(new_???(""));
	String suggestion=getSuggestionForUnknownTag(type, nodeType, validTags);
	return new_???(new_???(""+type.getSimpleName()+""+nodeType+""+""+validTagsList+""+suggestion, new_???(node)));
}
Option<Result<Object, CompileError>> findNestedSealedDeserialization_JavaSerializer(Class</*Wildcard[]*/> type, Node node, String nodeType) {
	Class</*Wildcard[]*/>* subclasses=type.getPermittedSubclasses();
	int j=/*???*/;
	while (/*???*/)
	{
	Class</*Wildcard[]*/> permitted=/*???*/;
	Option<Result<Object, CompileError>> recursiveResult=tryDeserializeNestedSealed(type, node, nodeType, permitted);
	if (/*???*/)return k;
	j++;}
	return new_???();
}
Option<Result<Object, CompileError>> tryDeserializeNestedSealed_JavaSerializer(Class</*Wildcard[]*/> type, Node node, String nodeType, Class</*Wildcard[]*/> permitted) {
	if (/*???*/)return new_???();
	Result<Object, CompileError> recursiveResult=deserializeSealed(permitted, node);
	if (/*???*/&&type.isAssignableFrom(value.getClass()))return new_???(recursiveResult);
	if (/*???*/&&canMatchType(permitted, nodeType))return new_???(recursiveResult);
	return new_???();
}
String getSuggestionForUnknownTag_JavaSerializer(Class</*Wildcard[]*/> type, String nodeType, List<String> validTags) {
	if (validTags.isEmpty())return "";
	/*???*/(nodeType, validTags);
	if (/*???*/)return ""+tag+""+nodeType+""+type.getSimpleName()+"";
	return ""+nodeType+""+type.getSimpleName()+"";
}
Option<String> findClosestTag_JavaSerializer(String nodeType, List<String> validTags) {
	/*???*/();
	int minDistance=Integer.MAX_VALUE;
	int i=/*???*/;
	while (/*???*/)
	{
	String tag=validTags.get(i).orElse(null);
	int distance=levenshteinDistance(nodeType.toLowerCase(), tag.toLowerCase());
	if (/*???*/)minDistance=distance;
	closest==Option.of(tag);
	i++;}
	return closest;
}
int levenshteinDistance_JavaSerializer(String s1, String s2) {
	int** dp=/*???*/;
	initializeFirstColumn(dp, s1);
	initializeFirstRow(dp, s2);
	fillLevenshteinMatrix(dp, s1, s2);
	return /*???*/;
}
void initializeFirstColumn_JavaSerializer(int** dp, String s1) {
	int bound=s1.length();
	int i1=/*???*/;
	while (/*???*/)
	{
	/*???*/=i1;
	i1++;}
}
void initializeFirstRow_JavaSerializer(int** dp, String s2) {
	int bound1=s2.length();
	int j=/*???*/;
	while (/*???*/)
	{
	/*???*/=j;
	j++;}
}
void fillLevenshteinMatrix_JavaSerializer(int** dp, String s1, String s2) {
	int i=/*???*/;
	while (/*???*/)
	{
	fillLevenshteinRow(dp, s1, s2, i);
	i++;}
}
void fillLevenshteinRow_JavaSerializer(int** dp, String s1, String s2, int i) {
	int j=/*???*/;
	while (/*???*/)
	{
	if (s1.charAt(/*???*/)==s2.charAt(/*???*/))/*???*/=/*???*/;
	else
	/*???*/==/*???*/+Math.min(/*???*/, Math.min(/*???*/, /*???*/));
	j++;}
}
List<String> collectAllValidTags_JavaSerializer(Class</*Wildcard[]*/> sealedType) {
	/*???*/=new_???();
	Arrays.stream(sealedType.getPermittedSubclasses()).forEach(/*???*/);
	return tags;
}
boolean canMatchType_JavaSerializer(Class</*Wildcard[]*/> sealedType, String nodeType) {
	Class</*Wildcard[]*/>* permittedSubclasses=sealedType.getPermittedSubclasses();
	int i=/*???*/;
	while (/*???*/)
	{
	Class</*Wildcard[]*/> permitted=/*???*/;
	/*???*/(permitted);
	if (/*???*/&&tag.equals(nodeType))return true;
	if (permitted.isSealed()&&/*???*/.isRecord())if (canMatchType(permitted, nodeType))return true;
	i++;}
	return false;
}
Result<Object, CompileError> deserializeRecord_JavaSerializer(Class</*Wildcard[]*/> type, Node node) {
	/*???*/(type);
	if (/*???*/)
	if (/*???*/)
	{
	if (/*???*/.is(expectedType0))return new_???(new_???(""+expectedType0+""+nodeType+"", new_???(node)));}
	else
	return new_???(new_???(""+type.getSimpleName()+""+expectedType0+"", new_???(node)));
	RecordComponent* components=type.getRecordComponents();
	Object* arguments=/*???*/;
	/*???*/=new_???();
	/*???*/=new_???();
	Stream.range(/*???*/, components.length).forEach(/*???*/);
	/*???*/(node, consumedFields, type);
	if (/*???*/)errors.addLast(error);
	if (/*???*/.isEmpty())return new_???(new_???(""+type.getSimpleName()+"", new_???(node), errors));/*???*//*???*/
}
Result<Object, CompileError> deserializeField_JavaSerializer(RecordComponent component, Node node, Set<String> consumedFields) {
	String fieldName=component.getName();
	Class</*Wildcard[]*/> fieldType=component.getType();
	if (fieldType==String.class)return deserializeStringField(fieldName, node, consumedFields);
	if (Option.class.isAssignableFrom(fieldType))return deserializeOptionField(component, node, consumedFields);
	if (magma.list.NonEmptyList.class.isAssignableFrom(fieldType))return deserializeNonEmptyListField(component, node, consumedFields);
	if (List.class.isAssignableFrom(fieldType))return deserializeListField(component, node, consumedFields);
	/*???*/(fieldName);
	if (/*???*/)
	{
	consumedFields.add(fieldName);
	return deserializeValue(fieldType, value);}
	else
	return new_???(new_???(""+fieldName+""+fieldType.getSimpleName()+"", new_???(node)));
}
Result<Object, CompileError> deserializeStringField_JavaSerializer(String fieldName, Node node, Set<String> consumedFields) {
	/*???*/(fieldName);
	if (/*???*/)
	{
	consumedFields.add(fieldName);
	return new_???(value);}
	/*???*/(node, fieldName);
	if (/*???*/)
	{
	consumedFields.add(fieldName);
	return new_???(value);}
	else
	return new_???(new_???(""+fieldName+"", new_???(node)));
}
Result<Object, CompileError> deserializeOptionField_JavaSerializer(RecordComponent component, Node node, Set<String> consumedFields) {
	Result<Type, CompileError> elementTypeResult=getGenericArgument(component.getGenericType());
	if (/*???*/)return new_???(error);
	Type elementType=/*???*/.value();
	Result<Class</*Wildcard[]*/>, CompileError> elementClassResult=erase(elementType);
	if (/*???*/)return new_???(error);
	Class</*Wildcard[]*/> elementClass=/*???*/.value();
	String fieldName=component.getName();
	if (elementClass==String.class)
	{
	/*???*/(fieldName);
	if (/*???*/)
	{
	consumedFields.add(fieldName);
	return new_???(direct);}
	/*???*/(node, fieldName);
	if (/*???*/)
	{
	consumedFields.add(fieldName);
	return new_???(nested);}
	/*???*/(fieldName);
	if (/*???*/)return new_???(new_???(""+fieldName+""+node.maybeType.orElse("")+"", new_???(node)));
	/*???*/(fieldName);
	if (/*???*/)return new_???(new_???(""+fieldName+""+node.maybeType.orElse("")+"", new_???(node)));
	return new_???(Option.empty());}
	if (List.class.isAssignableFrom(elementClass))return deserializeOptionListField(fieldName, elementType, node, consumedFields);
	/*???*/(fieldName);
	if (/*???*/)
	{
	consumedFields.add(fieldName);
	return deserializeValue(elementClass, value).mapValue(/*???*/);}
	else
	return new_???(Option.empty());
}
Result<Object, CompileError> deserializeOptionListField_JavaSerializer(String fieldName, Type listType, Node node, Set<String> consumedFields) {
	Result<Type, CompileError> elementTypeResult=getGenericArgument(listType);
	if (/*???*/)return new_???(error);
	Type elementType=/*???*/.value();
	Result<Class</*Wildcard[]*/>, CompileError> elementClassResult=erase(elementType);
	if (/*???*/)return new_???(error);
	Class</*Wildcard[]*/> elementClass=/*???*/.value();
	/*???*/(fieldName);
	if (/*???*/)
	{
	consumedFields.add(fieldName);
	Result<List<Object>, CompileError> elementsResult=deserializeListElements(elementClass, value);
	return elementsResult.mapValue(/*???*/.of(list.copy()));}
	else
	return new_???(Option.empty());
}
Result<Object, CompileError> deserializeNonEmptyListField_JavaSerializer(RecordComponent component, Node node, Set<String> consumedFields) {
	String fieldName=component.getName();
	Result<Type, CompileError> elementTypeResult=getGenericArgument(component.getGenericType());
	if (/*???*/)return new_???(error);
	Type elementType=/*???*/.value();
	Result<Class</*Wildcard[]*/>, CompileError> elementClassResult=erase(elementType);
	if (/*???*/)return new_???(error);
	Class</*Wildcard[]*/> elementClass=/*???*/.value();
	/*???*/(fieldName);
	if (/*???*/)
	{
	consumedFields.add(fieldName);
	if (value.isEmpty())return new_???(new_???(""+fieldName+"", new_???(node)));
	Result<List<Object>, CompileError> elementsResult=deserializeListElements(elementClass, value);
	if (/*???*/)return new_???(error);
	/*???*/();
	/*???*/(elements);
	if (/*???*/)return new_???(nel);
	else
	return new_???(new_???(""+fieldName+"", new_???(node)));}
	else
	return new_???(new_???(""+fieldName+"", new_???(node)));
}
Result<Object, CompileError> deserializeListField_JavaSerializer(RecordComponent component, Node node, Set<String> consumedFields) {
	String fieldName=component.getName();
	Result<Type, CompileError> elementTypeResult=getGenericArgument(component.getGenericType());
	if (/*???*/)return new_???(error);
	Type elementType=/*???*/.value();
	Result<Class</*Wildcard[]*/>, CompileError> elementClassResult=erase(elementType);
	if (/*???*/)return new_???(error);
	Class</*Wildcard[]*/> elementClass=/*???*/.value();
	/*???*/(fieldName);
	if (/*???*/)
	{
	consumedFields.add(fieldName);
	Result<List<Object>, CompileError> elementsResult=deserializeListElements(elementClass, value);
	return elementsResult.mapValue(/*???*/);}
	else
	return new_???(new_???(""+fieldName+"", new_???(node)));
}
Result<List<Object>, CompileError> deserializeListElements_JavaSerializer(Class</*Wildcard[]*/> elementClass, List<Node> nodeList) {
	/*???*/=new_???();
	/*???*/=new_???();
	int index=/*???*/;
	int i=/*???*/;
	while (/*???*/)
	{
	Node childNode=nodeList.get(i).orElse(null);
	Result<Object, CompileError> childResult=deserializeValue(elementClass, childNode);
	if (/*???*/)results.addLast(value);
	else
	if (/*???*/)
	if (elementClass.isSealed()&&/*???*/)
	{
	CompileError wrappedError=new_???(""+index+""+nodeType+""+elementClass.getSimpleName()+"", new_???(childNode), List.of(error));
	errors.addLast(wrappedError);}
	else
	if (shouldBeDeserializableAs(childNode, elementClass))errors.addLast(error);
	index++;
	i++;}
	if (errors.isEmpty())return new_???(results);
	return new_???(new_???(""+errors.size()+""+nodeList.size()+""+elementClass.getSimpleName()+"", new_???(nodeList.getFirst().orElse(null)), errors));
}
Node createNodeWithType_JavaSerializer(Class</*Wildcard[]*/> type) {
	Node node=new_???();
	/*???*/(type);
	if (/*???*/)node.retype(value);
	return node;
}
Node mergeNodes_JavaSerializer(Node base, Node addition) {
	Node result=new_???();
	result.maybeType=base.maybeType;
	result.merge(base);
	result.merge(addition);
	return result;
}
Result<Type, CompileError> getGenericArgument_JavaSerializer(Type type) {
	if (/*???*/)
	{
	Type* args=parameterized.getActualTypeArguments();
	if (/*???*/)return new_???(/*???*/);}
	return new_???(new_???(""+type+"", new_???(type.toString())));
}
Result<Class</*Wildcard[]*/>, CompileError> erase_JavaSerializer(Type type) {
	if (/*???*/)return new_???(clazz);
	if (/*???*/&&/*???*/)return new_???(raw);
	return new_???(new_???(""+type+"", new_???(type.toString())));
}
Option<String> resolveTypeIdentifier_JavaSerializer(Class</*Wildcard[]*/> clazz) {
	Tag annotation=clazz.getAnnotation(Tag.class);
	if (Objects.isNull(annotation))return Option.empty();
	return Option.of(annotation.value());
}
Option<String> findStringInChildren_JavaSerializer(Node node, String key) {
	{
	/*???*/();
	while (iterator.hasNext())
	{
	Node child=iterator.next();
	/*???*/(key);
	if (/*???*/)return result;
	result==findStringInChildren(child, key);
	if (/*???*/)return result;}}
	return findStringInNodeLists(node, key);
}
Option<String> findStringInNodeLists_JavaSerializer(Node node, String key) {
	/*???*/();
	while (iterator.hasNext())
	{
	/*???*/();
	/*???*/(children, key);
	if (/*???*/)return result;}
	return Option.empty();
}
Option<String> searchChildrenList_JavaSerializer(List<Node> children, String key) {
	int i=/*???*/;
	while (/*???*/)
	{
	Node child=children.get(i).orElse(null);
	/*???*/(key);
	if (/*???*/)return result;
	result==findStringInChildren(child, key);
	if (/*???*/)return result;
	i++;}
	return Option.empty();
}
boolean shouldBeDeserializableAs_JavaSerializer(Node node, Class</*Wildcard[]*/> targetClass) {
	if (/*???*/)return false;
	if (/*???*/)
	{
	Tag tagAnnotation=targetClass.getAnnotation(Tag.class);
	if (Objects.nonNull(tagAnnotation))return nodeType.equals(tagAnnotation.value());
	String targetName=targetClass.getSimpleName().toLowerCase();
	return /*???*/;}
	return false;
}
Option<CompileError> validateAllFieldsConsumed_JavaSerializer(Node node, Set<String> consumedFields, Class</*Wildcard[]*/> targetClass) {
	/*???*/=new_???();
	allFields.addAll(getStringKeys(node));
	allFields.addAll(node.nodes.keySet());
	allFields.addAll(node.nodeLists.keySet());
	/*???*/=new_???(allFields);
	leftoverFields.removeAll(consumedFields);
	if (/*???*/.isEmpty())
	{
	String leftoverList=String.join("", leftoverFields);
	return Option.of(new_???(""+targetClass.getSimpleName()+""+leftoverList+""+"", new_???(node)));}
	return Option.empty();
}
Set<String> getStringKeys_JavaSerializer(Node node) {
	return node.getStringKeys();
}
