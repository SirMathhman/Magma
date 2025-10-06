// Generated transpiled C++ from 'src\main\java\magma\compile\JavaSerializer.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct JavaSerializer {};
Result<> deserialize_JavaSerializer(Class<> clazz, Node node) {
	if (Objects.isNull(clazz))return new_???(new_???("", new_???(node)));
	if (Objects.isNull(node))return new_???(new_???("", new_???(clazz.getName())));
	return deserializeValue(clazz, node).mapValue(/*???*/);
}
Result<> serialize_JavaSerializer(Class<> clazz, T value) {
	if (Objects.isNull(clazz))return new_???(new_???("", new_???("")));
	if (Objects.isNull(value))return new_???(new_???(""+clazz.getName()+"", new_???("")));
	return serializeValue(clazz, value);
}
Result<> serializeValue_JavaSerializer(Class<> type, Object value) {
	if (type.isSealed()&&/*???*/.isRecord())return serializeSealed(type, value);
	if (/*???*/.isRecord())return new_???(new_???(""+type.getName()+"", new_???(type.getName())));
	return serializeRecord(type, value);
}
Result<> serializeSealed_JavaSerializer(Class<> type, Object value) {
	Class<> concreteClass=value.getClass();
	if (/*???*/.isAssignableFrom(concreteClass))return new_???(new_???(""+concreteClass.getName()+""+type.getName()+"", new_???(concreteClass.getName())));
	return serializeValue(concreteClass, value);
}
Result<> serializeRecord_JavaSerializer(Class<> type, Object value) {
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
Result<> serializeField_JavaSerializer(RecordComponent component, Object value) {
	char* fieldName=component.getName();
	Class<> fieldType=component.getType();
	if (Objects.isNull(value))return new_???(new_???(""+fieldName+"", new_???(fieldName)));
	if (fieldType==String.class)return new_???(new_???(fieldName, /*???*/));
	if (Option.class.isAssignableFrom(fieldType))return serializeOptionField(component, value);
	if (List.class.isAssignableFrom(fieldType))return serializeListField(component, value);
	return serializeValue(fieldType, value).mapValue(/*???*/.withNode(fieldName, childNode));
}
Result<> serializeOptionField_JavaSerializer(RecordComponent component, Object value) {
	char* fieldName=component.getName();
	if (/*???*/)return new_???(new_???(""+fieldName+"", new_???(fieldName)));
	if (/*???*/)return new_???(new_???());
	if (/*???*/)
	{
	Result<> elementTypeResult=getGenericArgument(component.getGenericType());
	if (/*???*/)return new_???(error);
	Type elementType=/*???*/.value();
	Result<> elementClassResult=erase(elementType);
	if (/*???*/)return new_???(error);
	Class<> elementClass=/*???*/.value();
	if (elementClass==String.class)return new_???(new_???(fieldName, /*???*/));
	if (List.class.isAssignableFrom(elementClass))return serializeOptionListField(fieldName, elementType, value1);
	return serializeValue(elementClass, value1).mapValue(/*???*/.withNode(fieldName, childNode));}
	return new_???(new_???());
}
Result<> serializeOptionListField_JavaSerializer(char* fieldName, Type listType, Object content) {
	if (/*???*/)return new_???(new_???(""+fieldName+"", new_???(fieldName)));
	Result<> elementTypeResult=getGenericArgument(listType);
	if (/*???*/)return new_???(error);
	Type elementType=/*???*/.value();
	Result<> elementClassResult=erase(elementType);
	if (/*???*/)return new_???(error);
	Class<> elementClass=/*???*/.value();
	return serializeListElements(elementClass, list).mapValue(/*???*/);
}
Result<> serializeListField_JavaSerializer(RecordComponent component, Object value) {
	char* fieldName=component.getName();
	if (/*???*/)return new_???(new_???(""+fieldName+"", new_???(fieldName)));
	Result<> elementTypeResult=getGenericArgument(component.getGenericType());
	if (/*???*/)return new_???(error);
	Type elementType=/*???*/.value();
	Result<> elementClassResult=erase(elementType);
	if (/*???*/)return new_???(error);
	Class<> elementClass=/*???*/.value();
	return serializeListElements(elementClass, list).mapValue(/*???*/);
}
Result<> serializeListElements_JavaSerializer(Class<> elementClass, List<> list) {
	/*???*/=new_???();
	/*???*/=new_???();
	list.stream().map(/*???*/(elementClass, element)).forEach(/*???*/);
	if (errors.isEmpty())return new_???(nodes);
	return new_???(new_???("", new_???(""), errors));
}
Result<> deserializeValue_JavaSerializer(Class<> type, Node node) {
	if (type.isSealed()&&/*???*/.isRecord())return deserializeSealed(type, node);
	if (/*???*/.isRecord())return new_???(new_???(""+type.getName()+"", new_???(node)));
	return deserializeRecord(type, node);
}
Result<> deserializeSealed_JavaSerializer(Class<> type, Node node) {
	if (/*???*/)return new_???(new_???(""+type.getName()+"", new_???(node)));
	Option<> directResult=tryDirectPermittedSubclasses(type, node, nodeType);
	return result;
	return tryNestedSealedInterfaces(type, node, nodeType);
}
Option<> tryDirectPermittedSubclasses_JavaSerializer(Class<> type, Node node, char* nodeType) {
	Class<>* permittedSubclasses=type.getPermittedSubclasses();
	int i=/*???*/;
	while (/*???*/)
	{
	Class<> permitted=/*???*/;
	/*???*/=resolveTypeIdentifier(permitted);
	if (/*???*/&&identifier.equals(nodeType))return new_???(deserializeValue(permitted, node));
	i++;}
	return new_???();
}
Result<> tryNestedSealedInterfaces_JavaSerializer(Class<> type, Node node, char* nodeType) {
	Option<> recursiveResult=findNestedSealedDeserialization(type, node, nodeType);
	return value;
	/*???*/=collectAllValidTags(type);
	char* validTagsList;
	if (validTags.isEmpty())validTagsList="";
	else validTagsList=String.join("", validTags);
	char* suggestion=getSuggestionForUnknownTag(type, nodeType, validTags);
	return new_???(new_???(""+type.getSimpleName()+""+nodeType+""+""+validTagsList+""+suggestion, new_???(node)));
}
Option<> findNestedSealedDeserialization_JavaSerializer(Class<> type, Node node, char* nodeType) {
	Class<>* subclasses=type.getPermittedSubclasses();
	int j=/*???*/;
	while (/*???*/)
	{
	Class<> permitted=/*???*/;
	Option<> recursiveResult=tryDeserializeNestedSealed(type, node, nodeType, permitted);
	return k;
	j++;}
	return new_???();
}
Option<> tryDeserializeNestedSealed_JavaSerializer(Class<> type, Node node, char* nodeType, Class<> permitted) {
	if (/*???*/)return new_???();
	Result<> recursiveResult=deserializeSealed(permitted, node);
	if (/*???*/&&type.isAssignableFrom(value.getClass()))return new_???(recursiveResult);
	if (/*???*/&&canMatchType(permitted, nodeType))return new_???(recursiveResult);
	return new_???();
}
char* getSuggestionForUnknownTag_JavaSerializer(Class<> type, char* nodeType, List<> validTags) {
	if (validTags.isEmpty())return "";
	/*???*/=findClosestTag(nodeType, validTags);
	if (/*???*/)return ""+tag+""+nodeType+""+type.getSimpleName()+"";
	return ""+nodeType+""+type.getSimpleName()+"";
}
Option<> findClosestTag_JavaSerializer(char* nodeType, List<> validTags) {
	/*???*/=Option.empty();
	int minDistance=Integer.MAX_VALUE;
	int i=/*???*/;
	while (/*???*/)
	{
	char* tag=validTags.get(i);
	int distance=levenshteinDistance(nodeType.toLowerCase(), tag.toLowerCase());
	if (/*???*/)minDistance=distance;
	closest=Option.of(tag);
	i++;}
	return closest;
}
int levenshteinDistance_JavaSerializer(char* s1, char* s2) {
	int** dp=/*???*/;
	int bound=s1.length();
	int i1=/*???*/;
	while (/*???*/)
	{
	/*???*/=i1;
	i1++;}
	int bound1=s2.length();
	int j=/*???*/;
	while (/*???*/)
	{
	/*???*/=j;
	j++;}
	int i=/*???*/;
	while (/*???*/)
	{
	fillLevenshteinRow(dp, s1, s2, i);
	i++;}
	return /*???*/;
}
void fillLevenshteinRow_JavaSerializer(int** dp, char* s1, char* s2, int i) {
	int j=/*???*/;
	while (/*???*/)
	{
	if (s1.charAt(/*???*/)==s2.charAt(/*???*/))/*???*/=/*???*/;
	else
	/*???*/=/*???*/+Math.min(/*???*/, Math.min(/*???*/, /*???*/));
	j++;}
}
List<> collectAllValidTags_JavaSerializer(Class<> sealedType) {
	/*???*/=new_???();
	Arrays.stream(sealedType.getPermittedSubclasses()).forEach(/*???*/);
	return tags;
}
boolean canMatchType_JavaSerializer(Class<> sealedType, char* nodeType) {
	Class<>* permittedSubclasses=sealedType.getPermittedSubclasses();
	int i=/*???*/;
	while (/*???*/)
	{
	Class<> permitted=/*???*/;
	/*???*/=resolveTypeIdentifier(permitted);
	return true;
	return true;
	i++;}
	return false;
}
Result<> deserializeRecord_JavaSerializer(Class<> type, Node node) {
	/*???*/=resolveTypeIdentifier(type);
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
	IntStream.range(/*???*/, components.length).forEach(/*???*/);
	/*???*/=validateAllFieldsConsumed(node, consumedFields, type);
	if (/*???*/)errors.add(error);
	if (/*???*/.isEmpty())return new_???(new_???(""+type.getSimpleName()+"", new_???(node), errors));/*???*//*???*/
}
Result<> deserializeField_JavaSerializer(RecordComponent component, Node node, Set<> consumedFields) {
	char* fieldName=component.getName();
	Class<> fieldType=component.getType();
	if (fieldType==String.class)return deserializeStringField(fieldName, node, consumedFields);
	if (Option.class.isAssignableFrom(fieldType))return deserializeOptionField(component, node, consumedFields);
	if (List.class.isAssignableFrom(fieldType))return deserializeListField(component, node, consumedFields);
	/*???*/=node.findNode(fieldName);
	if (/*???*/)
	{
	consumedFields.add(fieldName);
	return deserializeValue(fieldType, value);}
	else
	return new_???(new_???(""+fieldName+""+fieldType.getSimpleName()+"", new_???(node)));
}
Result<> deserializeStringField_JavaSerializer(char* fieldName, Node node, Set<> consumedFields) {
	/*???*/=node.findString(fieldName);
	if (/*???*/)
	{
	consumedFields.add(fieldName);
	return new_???(value);}
	/*???*/=findStringInChildren(node, fieldName);
	if (/*???*/)
	{
	consumedFields.add(fieldName);
	return new_???(value);}
	else
	return new_???(new_???(""+fieldName+"", new_???(node)));
}
Result<> deserializeOptionField_JavaSerializer(RecordComponent component, Node node, Set<> consumedFields) {
	Result<> elementTypeResult=getGenericArgument(component.getGenericType());
	if (/*???*/)return new_???(error);
	Type elementType=/*???*/.value();
	Result<> elementClassResult=erase(elementType);
	if (/*???*/)return new_???(error);
	Class<> elementClass=/*???*/.value();
	char* fieldName=component.getName();
	if (elementClass==String.class)
	{
	/*???*/=node.findString(fieldName);
	if (/*???*/)
	{
	consumedFields.add(fieldName);
	return new_???(direct);}
	/*???*/=findStringInChildren(node, fieldName);
	if (/*???*/)
	{
	consumedFields.add(fieldName);
	return new_???(nested);}
	/*???*/=node.findNode(fieldName);
	if (/*???*/)return new_???(new_???(""+fieldName+""+node.maybeType.orElse("")+"", new_???(node)));
	Option<> wrongTypeList=node.findNodeList(fieldName);
	if (/*???*/)return new_???(new_???(""+fieldName+""+node.maybeType.orElse("")+"", new_???(node)));
	return new_???(Option.empty());}
	if (List.class.isAssignableFrom(elementClass))return deserializeOptionListField(fieldName, elementType, node, consumedFields);
	/*???*/=node.findNode(fieldName);
	if (/*???*/)
	{
	consumedFields.add(fieldName);
	return deserializeValue(elementClass, value).mapValue(/*???*/);}
	else
	return new_???(Option.empty());
}
Result<> deserializeOptionListField_JavaSerializer(char* fieldName, Type listType, Node node, Set<> consumedFields) {
	Result<> elementTypeResult=getGenericArgument(listType);
	if (/*???*/)return new_???(error);
	Type elementType=/*???*/.value();
	Result<> elementClassResult=erase(elementType);
	if (/*???*/)return new_???(error);
	Class<> elementClass=/*???*/.value();
	Option<> maybeList=node.findNodeList(fieldName);
	if (/*???*/)
	{
	consumedFields.add(fieldName);
	Result<> elementsResult=deserializeListElements(elementClass, value);
	return elementsResult.mapValue(/*???*/.of(List.copyOf(list)));}
	else
	return new_???(Option.empty());
}
Result<> deserializeListField_JavaSerializer(RecordComponent component, Node node, Set<> consumedFields) {
	char* fieldName=component.getName();
	Result<> elementTypeResult=getGenericArgument(component.getGenericType());
	if (/*???*/)return new_???(error);
	Type elementType=/*???*/.value();
	Result<> elementClassResult=erase(elementType);
	if (/*???*/)return new_???(error);
	Class<> elementClass=/*???*/.value();
	Option<> maybeList=node.findNodeList(fieldName);
	if (/*???*/)
	{
	consumedFields.add(fieldName);
	Result<> elementsResult=deserializeListElements(elementClass, value);
	return elementsResult.mapValue(/*???*/);}
	else
	return new_???(new_???(""+fieldName+"", new_???(node)));
}
Result<> deserializeListElements_JavaSerializer(Class<> elementClass, List<> nodeList) {
	/*???*/=new_???();
	/*???*/=new_???();
	int index=/*???*/;
	int i=/*???*/;
	while (/*???*/)
	{
	Node childNode=nodeList.get(i);
	Result<> childResult=deserializeValue(elementClass, childNode);
	if (/*???*/)results.add(value);
	else
	if (/*???*/)
	if (elementClass.isSealed()&&/*???*/)
	{
	CompileError wrappedError=new_???(""+index+""+nodeType+""+elementClass.getSimpleName()+"", new_???(childNode), List.of(error));
	errors.add(wrappedError);}
	else
	if (shouldBeDeserializableAs(childNode, elementClass))errors.add(error);
	index++;
	i++;}
	if (errors.isEmpty())return new_???(results);
	return new_???(new_???(""+errors.size()+""+nodeList.size()+""+elementClass.getSimpleName()+"", new_???(nodeList.getFirst()), errors));
}
Node createNodeWithType_JavaSerializer(Class<> type) {
	Node node=new_???();
	/*???*/=resolveTypeIdentifier(type);
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
Result<> getGenericArgument_JavaSerializer(Type type) {
	if (/*???*/)
	{
	Type* args=parameterized.getActualTypeArguments();
	if (/*???*/)return new_???(/*???*/);}
	return new_???(new_???(""+type+"", new_???(type.toString())));
}
Result<> erase_JavaSerializer(Type type) {
	if (/*???*/)return new_???(clazz);
	if (/*???*/&&/*???*/)return new_???(raw);
	return new_???(new_???(""+type+"", new_???(type.toString())));
}
Option<> resolveTypeIdentifier_JavaSerializer(Class<> clazz) {
	Tag annotation=clazz.getAnnotation(Tag.class);
	if (Objects.isNull(annotation))return Option.empty();
	return Option.of(annotation.value());
}
Option<> findStringInChildren_JavaSerializer(Node node, char* key) {
	{
	/*???*/=node.nodes.values().iterator();
	while (iterator.hasNext())
	{
	Node child=iterator.next();
	/*???*/=child.findString(key);
	return result;
	result=findStringInChildren(child, key);
	return result;}}
	return findStringInNodeLists(node, key);
}
Option<> findStringInNodeLists_JavaSerializer(Node node, char* key) {
	Iterator<> iterator=node.nodeLists.values().iterator();
	while (iterator.hasNext())
	{
	/*???*/=iterator.next();
	/*???*/=searchChildrenList(children, key);
	return result;}
	return Option.empty();
}
Option<> searchChildrenList_JavaSerializer(List<> children, char* key) {
	int i=/*???*/;
	while (/*???*/)
	{
	Node child=children.get(i);
	/*???*/=child.findString(key);
	return result;
	result=findStringInChildren(child, key);
	return result;
	i++;}
	return Option.empty();
}
boolean shouldBeDeserializableAs_JavaSerializer(Node node, Class<> targetClass) {
	return false;
	if (/*???*/)
	{
	Tag tagAnnotation=targetClass.getAnnotation(Tag.class);
	if (Objects.nonNull(tagAnnotation))return nodeType.equals(tagAnnotation.value());
	char* targetName=targetClass.getSimpleName().toLowerCase();
	return /*???*/;}
	return false;
}
Option<> validateAllFieldsConsumed_JavaSerializer(Node node, Set<> consumedFields, Class<> targetClass) {
	/*???*/=new_???();
	allFields.addAll(getStringKeys(node));
	allFields.addAll(node.nodes.keySet());
	allFields.addAll(node.nodeLists.keySet());
	/*???*/=new_???(allFields);
	leftoverFields.removeAll(consumedFields);
	if (/*???*/.isEmpty())
	{
	char* leftoverList=String.join("", leftoverFields);
	return Option.of(new_???(""+targetClass.getSimpleName()+""+leftoverList+""+"", new_???(node)));}
	return Option.empty();
}
Set<> getStringKeys_JavaSerializer(Node node) {
	return node.getStringKeys();
}
