// Generated transpiled C++ from 'src\main\java\magma\compile\rule\FoldingDivider.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct FoldingDivider {/*???*/ folder;};
/*???*/ FoldingDivider_FoldingDivider(/*???*/ folder) {
	/*???*/ folder;
}
/*???*/ divide_FoldingDivider(/*???*/ input) {
	/*???*/ current=new_???(input);
	while (true)
	{
	/*???*/ pop=current.pop();
	/*???*/ break;
	/*???*/ current=folder.fold(current, c);}
	return current.advance().stream();
}
/*???*/ delimiter_FoldingDivider() {
	return folder.delimiter();
}
