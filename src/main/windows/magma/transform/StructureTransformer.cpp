// Generated transpiled C++ from 'src\main\java\magma\transform\StructureTransformer.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct StructureTransformer {};
List<> flattenStructure_StructureTransformer(/*???*/ aClass) {
	List<> children=aClass.children();
	ArrayList<> segments=new_???();
	ArrayList<> fields=new_???();
	addRecordParamsAsFields(aClass, fields);
	char* name=aClass.name();
	children.stream().map(/*???*/.flattenStructureSegment(child, name)).forEach(/*???*/);
	/*???*/ structure=new_???(name, fields, new_???(System.lineSeparator()), aClass.typeParameters());
	List<> copy=new_???();
	copy.add(structure);
	copy.addAll(segments);
	return copy;
}
void addRecordParamsAsFields_StructureTransformer(/*???*/ aClass, ArrayList<> fields) {
	if (/*???*/)
	{
	Option<> params=record.params();
	if (/*???*/)paramList.stream().map(/*???*/).forEach(/*???*/);}
}
