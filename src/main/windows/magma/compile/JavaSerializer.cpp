// Generated transpiled C++ from 'src\main\java\magma\compile\JavaSerializer.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct JavaSerializer {};
Result<T, CompileError> deserialize_JavaSerializer() {
	if (Objects.isNull())
	{
	return new_???();}
	if (Objects.isNull())
	{
	return new_???();}
	return deserializeValue().mapValue();
}
Result<Node, CompileError> serialize_JavaSerializer() {
	if (Objects.isNull())
	{
	return new_???();}
	if (Objects.isNull())
	{
	return new_???();}
	return serializeValue();
}
Result<Node, CompileError> serializeValue_JavaSerializer() {
	if (type.isSealed()&&/*???*/.isRecord())
	{
	return serializeSealed();}
	if (/*???*/.isRecord())
	{
	return new_???();}
	return serializeRecord();
}
Result<Node, CompileError> serializeSealed_JavaSerializer() {
	Class</*Wildcard[]*/> concreteClass=value.getClass();
	if (/*???*/.isAssignableFrom())
	{
	return new_???();}
	return serializeValue();
}
Result<Node, CompileError> serializeRecord_JavaSerializer() {
	Node result=createNodeWithType();
	/*???*/=new_???();
	RecordComponent* recordComponents=type.getRecordComponents();
	int i=/*???*/;
	while (/*???*/)
	{
	RecordComponent component=/*???*/;/*???*//*???*/
	i++;}
	if (errors.isEmpty())
	{
	return new_???();}
	return new_???();
}
InputContext createContext_JavaSerializer() {
	return new_???();
}
Result<Node, CompileError> serializeField_JavaSerializer() {
	String fieldName=component.getName();
	Class</*Wildcard[]*/> fieldType=component.getType();
	if (Objects.isNull())
	{
	return new_???();}
	if (fieldType==Slice.class)
	{
	return new_???();}
	if (Option.class.isAssignableFrom())
	{
	return serializeOptionField();}
	if (NonEmptyList.class.isAssignableFrom())
	{
	return serializeNonEmptyListField();}
	if (List.class.isAssignableFrom())
	{
	return serializeListField();}
	return serializeValue().mapValue();
}
Result<Node, CompileError> serializeOptionField_JavaSerializer() {
	String fieldName=component.getName();
	if (/*???*/)
	{
	return new_???();}
	if (/*???*/)
	{
	return new_???();}
	if (/*???*/)
	{
	Result<Type, CompileError> elementTypeResult=getGenericArgument();
	if (/*???*/)
	{
	return new_???();}
	Type elementType=/*???*/.value();
	Result<Class</*Wildcard[]*/>, CompileError> elementClassResult=erase();
	if (/*???*/)
	{
	return new_???();}
	Class</*Wildcard[]*/> elementClass=/*???*/.value();
	if (elementClass==Slice.class)
	{
	return new_???();}
	if (NonEmptyList.class.isAssignableFrom())
	{
	return serializeOptionNonEmptyListField();}
	if (List.class.isAssignableFrom())
	{
	return serializeOptionListField();}
	return serializeValue().mapValue();}
	return new_???();
}
Result<Node, CompileError> serializeOptionListField_JavaSerializer() {
	if (/*???*/)
	{
	return new_???();}
	Result<Type, CompileError> elementTypeResult=getGenericArgument();
	if (/*???*/)
	{
	return new_???();}
	Type elementType=/*???*/.value();
	Result<Class</*Wildcard[]*/>, CompileError> elementClassResult=erase();
	if (/*???*/)
	{
	return new_???();}
	Class</*Wildcard[]*/> elementClass=/*???*/.value();
	return serializeListElements().mapValue();
}
Result<Node, CompileError> serializeOptionNonEmptyListField_JavaSerializer() {
	if (/*???*/)
	{
	return new_???();}
	Result<Type, CompileError> elementTypeResult=getGenericArgument();
	if (/*???*/)
	{
	return new_???();}
	Type elementType=/*???*/.value();
	Result<Class</*Wildcard[]*/>, CompileError> elementClassResult=erase();
	if (/*???*/)
	{
	return new_???();}
	Class</*Wildcard[]*/> elementClass=/*???*/.value();
	return serializeListElements().mapValue();
}
Result<Node, CompileError> serializeNonEmptyListField_JavaSerializer() {
	String fieldName=component.getName();
	if (/*???*/)
	{
	return new_???();}
	Result<Type, CompileError> elementTypeResult=getGenericArgument();
	if (/*???*/)
	{
	return new_???();}
	Type elementType=/*???*/.value();
	Result<Class</*Wildcard[]*/>, CompileError> elementClassResult=erase();
	if (/*???*/)
	{
	return new_???();}
	Class</*Wildcard[]*/> elementClass=/*???*/.value();
	List</*Wildcard[]*/> list=nonEmptyList.toList();
	return serializeListElements().mapValue();
}
Result<Node, CompileError> serializeListField_JavaSerializer() {
	String fieldName=component.getName();
	if (/*???*/)
	{
	return new_???();}
	Result<Type, CompileError> elementTypeResult=getGenericArgument();
	if (/*???*/)
	{
	return new_???();}
	Type elementType=/*???*/.value();
	Result<Class</*Wildcard[]*/>, CompileError> elementClassResult=erase();
	if (/*???*/)
	{
	return new_???();}
	Class</*Wildcard[]*/> elementClass=/*???*/.value();
	return serializeListElements().mapValue();
}
Result<List<Node>, CompileError> serializeListElements_JavaSerializer() {
	/*???*/=new_???();
	/*???*/=new_???();
	list.stream().map().forEach();
	if (errors.isEmpty())
	{
	return new_???();}
	return new_???();
}
Result<Object, CompileError> deserializeValue_JavaSerializer() {
	if (type.isSealed()&&/*???*/.isRecord())
	{
	return deserializeSealed();}
	if (/*???*/.isRecord())
	{
	return new_???();}
	return deserializeRecord();
}
Result<Object, CompileError> deserializeSealed_JavaSerializer() {
	if (/*???*/)
	{
	return new_???();}
	Option<Result<Object, CompileError>> directResult=tryDirectPermittedSubclasses();
	return result;
	return tryNestedSealedInterfaces();
}
Option<Result<Object, CompileError>> tryDirectPermittedSubclasses_JavaSerializer() {
	Class</*Wildcard[]*/>* permittedSubclasses=type.getPermittedSubclasses();
	int i=/*???*/;
	while (/*???*/)
	{
	Class</*Wildcard[]*/> permitted=/*???*/;
	/*???*/();
	if (/*???*/&&identifier.equals())
	{
	return new_???();}
	i++;}
	return new_???();
}
Result<Object, CompileError> tryNestedSealedInterfaces_JavaSerializer() {
	Option<Result<Object, CompileError>> recursiveResult=findNestedSealedDeserialization();
	return value;
	/*???*/();
	String validTagsList;
	if (validTags.isEmpty())
	{
	validTagsList="";}
	else validTagsList=validTags.stream().collect();
	String suggestion=getSuggestionForUnknownTag();
	return new_???();
}
Option<Result<Object, CompileError>> findNestedSealedDeserialization_JavaSerializer() {
	Class</*Wildcard[]*/>* subclasses=type.getPermittedSubclasses();
	int j=/*???*/;
	while (/*???*/)
	{
	Class</*Wildcard[]*/> permitted=/*???*/;
	Option<Result<Object, CompileError>> recursiveResult=tryDeserializeNestedSealed();
	return k;
	j++;}
	return new_???();
}
Option<Result<Object, CompileError>> tryDeserializeNestedSealed_JavaSerializer() {
	if (/*???*/)
	{
	return new_???();}
	Result<Object, CompileError> recursiveResult=deserializeSealed();
	if (/*???*/&&type.isAssignableFrom())
	{
	return new_???();}
	if (/*???*/&&canMatchType())
	{
	return new_???();}
	return new_???();
}
String getSuggestionForUnknownTag_JavaSerializer() {
	if (validTags.isEmpty())
	{
	return "";}
	/*???*/();
	if (/*???*/)
	{
	return ""+tag+""+nodeType+""+type.getSimpleName()+"";}
	return ""+nodeType+""+type.getSimpleName()+"";
}
Option<String> findClosestTag_JavaSerializer() {
	/*???*/();
	int minDistance=Integer.MAX_VALUE;
	int i=/*???*/;
	while (/*???*/)
	{
	String tag=validTags.get().orElse();
	int distance=levenshteinDistance();
	if (/*???*/)
	{
	minDistance=distance;}
	closest==Option.of();
	i++;}
	return closest;
}
int levenshteinDistance_JavaSerializer() {
	int** dp=/*???*/;
	initializeFirstColumn();
	initializeFirstRow();
	fillLevenshteinMatrix();
	return /*???*/;
}
void initializeFirstColumn_JavaSerializer() {
	int bound=s1.length();
	int i1=/*???*/;
	while (/*???*/)
	{
	/*???*/=i1;
	i1++;}
}
void initializeFirstRow_JavaSerializer() {
	int bound1=s2.length();
	int j=/*???*/;
	while (/*???*/)
	{
	/*???*/=j;
	j++;}
}
void fillLevenshteinMatrix_JavaSerializer() {
	int i=/*???*/;
	while (/*???*/)
	{
	fillLevenshteinRow();
	i++;}
}
void fillLevenshteinRow_JavaSerializer() {
	int j=/*???*/;
	while (/*???*/)
	{
	if (s1.charAt()==s2.charAt())
	{
	/*???*/=/*???*/;}
	else
	/*???*/==/*???*/+Math.min();
	j++;}
}
List<String> collectAllValidTags_JavaSerializer() {
	/*???*/=new_???();
	Arrays.stream().forEach();
	return tags;
}
boolean canMatchType_JavaSerializer() {
	Class</*Wildcard[]*/>* permittedSubclasses=sealedType.getPermittedSubclasses();
	int i=/*???*/;
	while (/*???*/)
	{
	Class</*Wildcard[]*/> permitted=/*???*/;
	/*???*/();
	return true;
	return true;
	i++;}
	return false;
}
Result<Object, CompileError> deserializeRecord_JavaSerializer() {
	/*???*/();
	if (/*???*/)
	{
	if (/*???*/)
	{
	if (/*???*/.is())
	{
	return new_???();}}}
	else
	return new_???();
	RecordComponent* components=type.getRecordComponents();
	Object* arguments=/*???*/;
	/*???*/=new_???();
	/*???*/=new_???();
	Stream.range().forEach();
	/*???*/();
	if (/*???*/)
	{
	errors.addLast();}
	if (/*???*/.isEmpty())
	{
	return new_???();}/*???*//*???*/
}
Result<Object, CompileError> deserializeField_JavaSerializer() {
	String fieldName=component.getName();
	Class</*Wildcard[]*/> fieldType=component.getType();
	if (fieldType==Slice.class)
	{
	return deserializeSliceField();}
	if (Option.class.isAssignableFrom())
	{
	return deserializeOptionField();}
	if (NonEmptyList.class.isAssignableFrom())
	{
	return deserializeNonEmptyListField();}
	if (List.class.isAssignableFrom())
	{
	return deserializeListField();}
	/*???*/();
	if (/*???*/)
	{
	consumedFields.add();
	return deserializeValue();}
	else
	return new_???();
}
Result<Object, CompileError> deserializeSliceField_JavaSerializer() {
	/*???*/();
	if (/*???*/)
	{
	consumedFields.add();
	return new_???();}
	/*???*/();
	if (/*???*/)
	{
	consumedFields.add();
	return new_???();}
	else
	return new_???();
}
Result<Object, CompileError> deserializeOptionField_JavaSerializer() {
	Result<Type, CompileError> elementTypeResult=getGenericArgument();
	if (/*???*/)
	{
	return new_???();}
	Type elementType=/*???*/.value();
	Result<Class</*Wildcard[]*/>, CompileError> elementClassResult=erase();
	if (/*???*/)
	{
	return new_???();}
	Class</*Wildcard[]*/> elementClass=/*???*/.value();
	String fieldName=component.getName();
	if (elementClass==Slice.class)
	{
	/*???*/();
	if (/*???*/)
	{
	consumedFields.add();
	return new_???();}
	/*???*/();
	if (/*???*/)
	{
	consumedFields.add();
	return new_???();}
	/*???*/();
	if (/*???*/)
	{
	return new_???();}
	/*???*/();
	if (/*???*/)
	{
	return new_???();}
	return new_???();}
	if (NonEmptyList.class.isAssignableFrom())
	{
	return deserializeOptionNonEmptyListField();}
	if (List.class.isAssignableFrom())
	{
	return deserializeOptionListField();}
	/*???*/();
	if (/*???*/)
	{
	consumedFields.add();
	return deserializeValue().mapValue();}
	else
	return new_???();
}
Result<Object, CompileError> deserializeOptionListField_JavaSerializer() {
	Result<Type, CompileError> elementTypeResult=getGenericArgument();
	if (/*???*/)
	{
	return new_???();}
	Type elementType=/*???*/.value();
	Result<Class</*Wildcard[]*/>, CompileError> elementClassResult=erase();
	if (/*???*/)
	{
	return new_???();}
	Class</*Wildcard[]*/> elementClass=/*???*/.value();
	/*???*/();
	if (/*???*/)
	{
	consumedFields.add();
	Result<List<Object>, CompileError> elementsResult=deserializeListElements();
	return elementsResult.mapValue();}
	else
	return new_???();
}
Result<Object, CompileError> deserializeOptionNonEmptyListField_JavaSerializer() {
	Result<Type, CompileError> elementTypeResult=getGenericArgument();
	if (/*???*/)
	{
	return new_???();}
	Type elementType=/*???*/.value();
	Result<Class</*Wildcard[]*/>, CompileError> elementClassResult=erase();
	if (/*???*/)
	{
	return new_???();}
	Class</*Wildcard[]*/> elementClass=/*???*/.value();
	/*???*/();
	if (/*???*/)
	{
	consumedFields.add();
	Result<List<Object>, CompileError> elementsResult=deserializeListElements();
	if (/*???*/)
	{
	return new_???();}
	/*???*/();
	/*???*/();
	if (/*???*/)
	{
	return new_???();}
	return new_???();}
	return new_???();
}
Result<Object, CompileError> deserializeNonEmptyListField_JavaSerializer() {
	String fieldName=component.getName();
	Result<Type, CompileError> elementTypeResult=getGenericArgument();
	if (/*???*/)
	{
	return new_???();}
	Type elementType=/*???*/.value();
	Result<Class</*Wildcard[]*/>, CompileError> elementClassResult=erase();
	if (/*???*/)
	{
	return new_???();}
	Class</*Wildcard[]*/> elementClass=/*???*/.value();
	/*???*/();
	if (/*???*/)
	{
	consumedFields.add();
	Result<List<Object>, CompileError> elementsResult=deserializeListElements();
	if (/*???*/)
	{
	return new_???();}
	/*???*/();
	/*???*/();
	if (/*???*/)
	{
	return new_???();}
	else
	return new_???();}
	else
	return new_???();
}
Result<Object, CompileError> deserializeListField_JavaSerializer() {
	String fieldName=component.getName();
	Result<Type, CompileError> elementTypeResult=getGenericArgument();
	if (/*???*/)
	{
	return new_???();}
	Type elementType=/*???*/.value();
	Result<Class</*Wildcard[]*/>, CompileError> elementClassResult=erase();
	if (/*???*/)
	{
	return new_???();}
	Class</*Wildcard[]*/> elementClass=/*???*/.value();
	/*???*/();
	if (/*???*/)
	{
	consumedFields.add();
	Result<List<Object>, CompileError> elementsResult=deserializeListElements();
	return elementsResult.mapValue();}
	else
	return new_???();
}
Result<List<Object>, CompileError> deserializeListElements_JavaSerializer() {
	/*???*/=new_???();
	/*???*/=new_???();
	int index=/*???*/;
	int i=/*???*/;
	while (/*???*/)
	{
	Node childNode=nodeList.get().orElse();
	Result<Object, CompileError> childResult=deserializeValue();
	if (/*???*/)
	{
	results.addLast();}
	else
	if (/*???*/)
	{
	if (elementClass.isSealed()&&/*???*/)
	{
	CompileError wrappedError=new_???();
	errors.addLast();}}
	else
	if (shouldBeDeserializableAs())
	{
	errors.addLast();}
	index++;
	i++;}
	if (errors.isEmpty())
	{
	return new_???();}
	return new_???();
}
Node createNodeWithType_JavaSerializer() {
	Node node=new_???();
	/*???*/();
	if (/*???*/)
	{
	node.retype();}
	return node;
}
Node mergeNodes_JavaSerializer() {
	Node result=new_???();
	result.maybeType=base.maybeType;
	result.merge();
	result.merge();
	return result;
}
Result<Type, CompileError> getGenericArgument_JavaSerializer() {
	if (/*???*/)
	{
	Type* args=parameterized.getActualTypeArguments();
	if (/*???*/)
	{
	return new_???();}}
	return new_???();
}
Result<Class</*Wildcard[]*/>, CompileError> erase_JavaSerializer() {
	if (/*???*/)
	{
	return new_???();}
	if (/*???*/&&/*???*/)
	{
	return new_???();}
	return new_???();
}
Option<String> resolveTypeIdentifier_JavaSerializer() {
	Tag annotation=clazz.getAnnotation();
	if (Objects.isNull())
	{
	return Option.empty();}
	return Option.of();
}
Option<Slice> findSliceInChildren_JavaSerializer() {
	{
	/*???*/();
	while (iterator.hasNext())
	{
	Node child=iterator.next();
	/*???*/();
	return result;
	result==findSliceInChildren();
	return result;}}
	return findSliceInNodeLists();
}
Option<Slice> findSliceInNodeLists_JavaSerializer() {
	/*???*/();
	while (iterator.hasNext())
	{
	/*???*/();
	/*???*/();
	return result;}
	return Option.empty();
}
Option<Slice> searchChildrenList_JavaSerializer() {
	int i=/*???*/;
	while (/*???*/)
	{
	Node child=children.get().orElse();
	/*???*/();
	return result;
	result==findSliceInChildren();
	return result;
	i++;}
	return Option.empty();
}
boolean shouldBeDeserializableAs_JavaSerializer() {
	return false;
	if (/*???*/)
	{
	Tag tagAnnotation=targetClass.getAnnotation();
	if (Objects.nonNull())
	{
	return nodeType.equals();}
	String targetName=targetClass.getSimpleName().toLowerCase();
	return /*???*/;}
	return false;
}
Option<CompileError> validateAllFieldsConsumed_JavaSerializer() {
	/*???*/=new_???();
	allFields.addAll();
	allFields.addAll();
	allFields.addAll();
	/*???*/=new_???();
	leftoverFields.removeAll();
	if (/*???*/.isEmpty())
	{
	String leftoverList=String.join();
	return Option.of();}
	return Option.empty();
}
Set<String> getStringKeys_JavaSerializer() {
	return node.getStringKeys();
}
