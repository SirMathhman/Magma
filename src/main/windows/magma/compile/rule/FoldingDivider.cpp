// Generated transpiled C++ from 'src\main\java\magma\compile\rule\FoldingDivider.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct FoldingDivider {Folder folder;};
public FoldingDivider_FoldingDivider(Folder folder) {
	this.folder=folder;
}
Stream<> divide_FoldingDivider(char* input) {
	DivideState current=new_???(input);
	while (true)
	{
	Option<> pop=current.pop();
	if (/*???*/)
	break
	if (/*???*/)current==folder.fold(current, c);}
	return current.advance().stream();
}
char* delimiter_FoldingDivider() {
	return folder.delimiter();
}
