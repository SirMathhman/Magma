// Generated transpiled C++ from 'src\main\java\magma\compile\JavaSerializer.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct JavaSerializer {};
Result<> deserialize_JavaSerializer(Class<> clazz, /*???*/ node) {
	if (Objects.isNull(clazz))return new_???(new_???("", new_???(node)));
	if (Objects.isNull(node))return new_???(new_???("", new_???(clazz.getName())));
	return deserializeValue(clazz, node).mapValue(/*???*/);
}
Result<> serialize_JavaSerializer(Class<> clazz, /*???*/ value) {
	if (Objects.isNull(clazz))return new_???(new_???("", new_???("")));
	if (Objects.isNull(value))return new_???(new_???(""+clazz.getName()+"", new_???("")));
	return serializeValue(clazz, value);
}
Result<> serializeValue_JavaSerializer(Class<> type, /*???*/ value) {
	if (type.isSealed()&&/*???*/.isRecord())return serializeSealed(type, value);
	if (/*???*/.isRecord())return new_???(new_???(""+type.getName()+"", new_???(type.getName())));
	return serializeRecord(type, value);
}
Result<> serializeSealed_JavaSerializer(Class<> type, /*???*/ value) {
	Class<> concreteClass=value.getClass();
	if (/*???*/.isAssignableFrom(concreteClass))return new_???(new_???(""+concreteClass.getName()+""+type.getName()+"", new_???(concreteClass.getName())));
	return serializeValue(concreteClass, value);
}
Result<> serializeRecord_JavaSerializer(Class<> type, /*???*/ value) {
	/*???*/ result=createNodeWithType(type);
	/*???*/=new_???();
	/*???*/* recordComponents=type.getRecordComponents();
	/*???*/ i=/*???*/;
	while (/*???*/)
	{
	/*???*/ component=/*???*/;/*???*//*???*/
	i++;}
	if (errors.isEmpty())return new_???(result);
	return new_???(new_???(""+type.getSimpleName()+"", new_???(type.getName()), errors));
}
Result<> serializeField_JavaSerializer(/*???*/ component, /*???*/ value) {
	/*???*/ fieldName=component.getName();
	Class<> fieldType=component.getType();
	if (Objects.isNull(value))return new_???(new_???(""+fieldName+"", new_???(fieldName)));
	if (fieldType==String.class)return new_???(new_???(fieldName, /*???*/));
	if (Option.class.isAssignableFrom(fieldType))return serializeOptionField(component, value);
	if (List.class.isAssignableFrom(fieldType))return serializeListField(component, value);
	return serializeValue(fieldType, value).mapValue(/*???*/.withNode(fieldName, childNode));
}
Result<> serializeOptionField_JavaSerializer(/*???*/ component, /*???*/ value) {
	/*???*/ fieldName=component.getName();
	if (/*???*/)return new_???(new_???(""+fieldName+"", new_???(fieldName)));
	if (/*???*/)return new_???(new_???());
	if (/*???*/)
	{
	Result<> elementTypeResult=getGenericArgument(component.getGenericType());
	if (/*???*/)return new_???(error);
	/*???*/ elementType=/*???*/.value();
	Result<> elementClassResult=erase(elementType);
	if (/*???*/)return new_???(error);
	Class<> elementClass=/*???*/.value();
	if (elementClass==String.class)return new_???(new_???(fieldName, /*???*/));
	if (List.class.isAssignableFrom(elementClass))return serializeOptionListField(fieldName, elementType, value1);
	return serializeValue(elementClass, value1).mapValue(/*???*/.withNode(fieldName, childNode));}
	return new_???(new_???());
}
Result<> serializeOptionListField_JavaSerializer(/*???*/ fieldName, /*???*/ listType, /*???*/ content) {
	if (/*???*/)return new_???(new_???(""+fieldName+"", new_???(fieldName)));
	Result<> elementTypeResult=getGenericArgument(listType);
	if (/*???*/)return new_???(error);
	/*???*/ elementType=/*???*/.value();
	Result<> elementClassResult=erase(elementType);
	if (/*???*/)return new_???(error);
	Class<> elementClass=/*???*/.value();
	return serializeListElements(elementClass, list).mapValue(/*???*/);
}
Result<> serializeListField_JavaSerializer(/*???*/ component, /*???*/ value) {
	/*???*/ fieldName=component.getName();
	if (/*???*/)return new_???(new_???(""+fieldName+"", new_???(fieldName)));
	Result<> elementTypeResult=getGenericArgument(component.getGenericType());
	if (/*???*/)return new_???(error);
	/*???*/ elementType=/*???*/.value();
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
Result<> deserializeValue_JavaSerializer(Class<> type, /*???*/ node) {
	if (type.isSealed()&&/*???*/.isRecord())return deserializeSealed(type, node);
	if (/*???*/.isRecord())return new_???(new_???(""+type.getName()+"", new_???(node)));
	return deserializeRecord(type, node);
}
Result<> deserializeSealed_JavaSerializer(Class<> type, /*???*/ node) {
	if (/*???*/)return new_???(new_???(""+type.getName()+"", new_???(node)));
	Option<> directResult=tryDirectPermittedSubclasses(type, node, nodeType);
	/*???*/ result;
	return tryNestedSealedInterfaces(type, node, nodeType);
}
Option<> tryDirectPermittedSubclasses_JavaSerializer(Class<> type, /*???*/ node, /*???*/ nodeType) {
	Class<>* permittedSubclasses=type.getPermittedSubclasses();
	/*???*/ i=/*???*/;
	while (/*???*/)
	{
	Class<> permitted=/*???*/;
	/*???*/(permitted);
	if (/*???*/&&identifier.equals(nodeType))return new_???(deserializeValue(permitted, node));
	i++;}
	return new_???();
}
Result<> tryNestedSealedInterfaces_JavaSerializer(Class<> type, /*???*/ node, /*???*/ nodeType) {
	Option<> recursiveResult=findNestedSealedDeserialization(type, node, nodeType);
	/*???*/ value;
	/*???*/(type);
	/*???*/ validTagsList;
	if (validTags.isEmpty())validTagsList="";
	/*???*/ validTagsList=String.join("", validTags);
	/*???*/ suggestion=getSuggestionForUnknownTag(type, nodeType, validTags);
	return new_???(new_???(""+type.getSimpleName()+""+nodeType+""+""+validTagsList+""+suggestion, new_???(node)));
}
Option<> findNestedSealedDeserialization_JavaSerializer(Class<> type, /*???*/ node, /*???*/ nodeType) {
	Class<>* subclasses=type.getPermittedSubclasses();
	/*???*/ j=/*???*/;
	while (/*???*/)
	{
	Class<> permitted=/*???*/;
	Option<> recursiveResult=tryDeserializeNestedSealed(type, node, nodeType, permitted);
	/*???*/ k;
	j++;}
	return new_???();
}
Option<> tryDeserializeNestedSealed_JavaSerializer(Class<> type, /*???*/ node, /*???*/ nodeType, Class<> permitted) {
	if (/*???*/)return new_???();
	Result<> recursiveResult=deserializeSealed(permitted, node);
	if (/*???*/&&type.isAssignableFrom(value.getClass()))return new_???(recursiveResult);
	if (/*???*/&&canMatchType(permitted, nodeType))return new_???(recursiveResult);
	return new_???();
}
/*???*/ getSuggestionForUnknownTag_JavaSerializer(Class<> type, /*???*/ nodeType, List<> validTags) {
	if (validTags.isEmpty())return "";
	/*???*/(nodeType, validTags);
	if (/*???*/)return ""+tag+""+nodeType+""+type.getSimpleName()+"";
	return ""+nodeType+""+type.getSimpleName()+"";
}
Option<> findClosestTag_JavaSerializer(/*???*/ nodeType, List<> validTags) {
	/*???*/();
	/*???*/ minDistance=Integer.MAX_VALUE;
	/*???*/ i=/*???*/;
	while (/*???*/)
	{
	/*???*/ tag=validTags.get(i);
	/*???*/ distance=levenshteinDistance(nodeType.toLowerCase(), tag.toLowerCase());
	if (/*???*/)minDistance=distance;
	closest==Option.of(tag);
	i++;}
	/*???*/ closest;
}
/*???*/ levenshteinDistance_JavaSerializer(/*???*/ s1, /*???*/ s2) {
	/*???*/** dp=/*???*/;
	/*???*/ bound=s1.length();
	/*???*/ i1=/*???*/;
	while (/*???*/)
	{
	/*???*/=i1;
	i1++;}
	/*???*/ bound1=s2.length();
	/*???*/ j=/*???*/;
	while (/*???*/)
	{
	/*???*/=j;
	j++;}
	/*???*/ i=/*???*/;
	while (/*???*/)
	{
	fillLevenshteinRow(dp, s1, s2, i);
	i++;}
	return /*???*/;
}
/*???*/ fillLevenshteinRow_JavaSerializer(/*???*/** dp, /*???*/ s1, /*???*/ s2, /*???*/ i) {
	/*???*/ j=/*???*/;
	while (/*???*/)
	{
	if (s1.charAt(/*???*/)==s2.charAt(/*???*/))/*???*/=/*???*/;
	else
	/*???*/==/*???*/+Math.min(/*???*/, Math.min(/*???*/, /*???*/));
	j++;}
}
List<> collectAllValidTags_JavaSerializer(Class<> sealedType) {
	/*???*/=new_???();
	Arrays.stream(sealedType.getPermittedSubclasses()).forEach(/*???*/);
	/*???*/ tags;
}
/*???*/ canMatchType_JavaSerializer(Class<> sealedType, /*???*/ nodeType) {
	Class<>* permittedSubclasses=sealedType.getPermittedSubclasses();
	/*???*/ i=/*???*/;
	while (/*???*/)
	{
	Class<> permitted=/*???*/;
	/*???*/(permitted);
	/*???*/ true;
	/*???*/ true;
	i++;}
	/*???*/ false;
}
Result<> deserializeRecord_JavaSerializer(Class<> type, /*???*/ node) {
	/*???*/(type);
	if (/*???*/)
	if (/*???*/)
	{
	if (/*???*/.is(expectedType0))return new_???(new_???(""+expectedType0+""+nodeType+"", new_???(node)));}
	else
	return new_???(new_???(""+type.getSimpleName()+""+expectedType0+"", new_???(node)));
	/*???*/* components=type.getRecordComponents();
	/*???*/* arguments=/*???*/;
	/*???*/=new_???();
	/*???*/=new_???();
	IntStream.range(/*???*/, components.length).forEach(/*???*/);
	/*???*/(node, consumedFields, type);
	if (/*???*/)errors.add(error);
	if (/*???*/.isEmpty())return new_???(new_???(""+type.getSimpleName()+"", new_???(node), errors));/*???*//*???*/
}
Result<> deserializeField_JavaSerializer(/*???*/ component, /*???*/ node, Set<> consumedFields) {
	/*???*/ fieldName=component.getName();
	Class<> fieldType=component.getType();
	if (fieldType==String.class)return deserializeStringField(fieldName, node, consumedFields);
	if (Option.class.isAssignableFrom(fieldType))return deserializeOptionField(component, node, consumedFields);
	if (List.class.isAssignableFrom(fieldType))return deserializeListField(component, node, consumedFields);
	/*???*/(fieldName);
	if (/*???*/)
	{
	consumedFields.add(fieldName);
	return deserializeValue(fieldType, value);}
	else
	return new_???(new_???(""+fieldName+""+fieldType.getSimpleName()+"", new_???(node)));
}
Result<> deserializeStringField_JavaSerializer(/*???*/ fieldName, /*???*/ node, Set<> consumedFields) {
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
Result<> deserializeOptionField_JavaSerializer(/*???*/ component, /*???*/ node, Set<> consumedFields) {
	Result<> elementTypeResult=getGenericArgument(component.getGenericType());
	if (/*???*/)return new_???(error);
	/*???*/ elementType=/*???*/.value();
	Result<> elementClassResult=erase(elementType);
	if (/*???*/)return new_???(error);
	Class<> elementClass=/*???*/.value();
	/*???*/ fieldName=component.getName();
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
	Option<> wrongTypeList=node.findNodeList(fieldName);
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
Result<> deserializeOptionListField_JavaSerializer(/*???*/ fieldName, /*???*/ listType, /*???*/ node, Set<> consumedFields) {
	Result<> elementTypeResult=getGenericArgument(listType);
	if (/*???*/)return new_???(error);
	/*???*/ elementType=/*???*/.value();
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
Result<> deserializeListField_JavaSerializer(/*???*/ component, /*???*/ node, Set<> consumedFields) {
	/*???*/ fieldName=component.getName();
	Result<> elementTypeResult=getGenericArgument(component.getGenericType());
	if (/*???*/)return new_???(error);
	/*???*/ elementType=/*???*/.value();
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
	/*???*/ index=/*???*/;
	/*???*/ i=/*???*/;
	while (/*???*/)
	{
	/*???*/ childNode=nodeList.get(i);
	Result<> childResult=deserializeValue(elementClass, childNode);
	if (/*???*/)results.add(value);
	else
	if (/*???*/)
	if (elementClass.isSealed()&&/*???*/)
	{
	/*???*/ wrappedError=new_???(""+index+""+nodeType+""+elementClass.getSimpleName()+"", new_???(childNode), List.of(error));
	errors.add(wrappedError);}
	else
	if (shouldBeDeserializableAs(childNode, elementClass))errors.add(error);
	index++;
	i++;}
	if (errors.isEmpty())return new_???(results);
	return new_???(new_???(""+errors.size()+""+nodeList.size()+""+elementClass.getSimpleName()+"", new_???(nodeList.getFirst()), errors));
}
/*???*/ createNodeWithType_JavaSerializer(Class<> type) {
	/*???*/ node=new_???();
	/*???*/(type);
	if (/*???*/)node.retype(value);
	/*???*/ node;
}
/*???*/ mergeNodes_JavaSerializer(/*???*/ base, /*???*/ addition) {
	/*???*/ result=new_???();
	result.maybeType=base.maybeType;
	result.merge(base);
	result.merge(addition);
	/*???*/ result;
}
Result<> getGenericArgument_JavaSerializer(/*???*/ type) {
	if (/*???*/)
	{
	/*???*/* args=parameterized.getActualTypeArguments();
	if (/*???*/)return new_???(/*???*/);}
	return new_???(new_???(""+type+"", new_???(type.toString())));
}
Result<> erase_JavaSerializer(/*???*/ type) {
	if (/*???*/)return new_???(clazz);
	if (/*???*/&&/*???*/)return new_???(raw);
	return new_???(new_???(""+type+"", new_???(type.toString())));
}
Option<> resolveTypeIdentifier_JavaSerializer(Class<> clazz) {
	/*???*/ annotation=clazz.getAnnotation(Tag.class);
	if (Objects.isNull(annotation))return Option.empty();
	return Option.of(annotation.value());
}
Option<> findStringInChildren_JavaSerializer(/*???*/ node, /*???*/ key) {
	{
	/*???*/();
	while (iterator.hasNext())
	{
	/*???*/ child=iterator.next();
	/*???*/(key);
	/*???*/ result;
	result==findStringInChildren(child, key);
	/*???*/ result;}}
	return findStringInNodeLists(node, key);
}
Option<> findStringInNodeLists_JavaSerializer(/*???*/ node, /*???*/ key) {
	Iterator<> iterator=node.nodeLists.values().iterator();
	while (iterator.hasNext())
	{
	/*???*/();
	/*???*/(children, key);
	/*???*/ result;}
	return Option.empty();
}
Option<> searchChildrenList_JavaSerializer(List<> children, /*???*/ key) {
	/*???*/ i=/*???*/;
	while (/*???*/)
	{
	/*???*/ child=children.get(i);
	/*???*/(key);
	/*???*/ result;
	result==findStringInChildren(child, key);
	/*???*/ result;
	i++;}
	return Option.empty();
}
/*???*/ shouldBeDeserializableAs_JavaSerializer(/*???*/ node, Class<> targetClass) {
	/*???*/ false;
	if (/*???*/)
	{
	/*???*/ tagAnnotation=targetClass.getAnnotation(Tag.class);
	if (Objects.nonNull(tagAnnotation))return nodeType.equals(tagAnnotation.value());
	/*???*/ targetName=targetClass.getSimpleName().toLowerCase();
	return /*???*/;}
	/*???*/ false;
}
Option<> validateAllFieldsConsumed_JavaSerializer(/*???*/ node, Set<> consumedFields, Class<> targetClass) {
	/*???*/=new_???();
	allFields.addAll(getStringKeys(node));
	allFields.addAll(node.nodes.keySet());
	allFields.addAll(node.nodeLists.keySet());
	/*???*/=new_???(allFields);
	leftoverFields.removeAll(consumedFields);
	if (/*???*/.isEmpty())
	{
	/*???*/ leftoverList=String.join("", leftoverFields);
	return Option.of(new_???(""+targetClass.getSimpleName()+""+leftoverList+""+"", new_???(node)));}
	return Option.empty();
}
Set<> getStringKeys_JavaSerializer(/*???*/ node) {
	return node.getStringKeys();
}
