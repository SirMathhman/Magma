// Generated transpiled C++ from 'src\main\java\magma\compile\rule\FoldingDivider.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct FoldingDivider {/*private final Folder*/ folder;};
/*public*/ FoldingDivider_FoldingDivider(/*Folder*/ folder) {
	/*this.folder */=/* folder*/;
}
/*@Override
	public Stream<String>*/ divide_FoldingDivider(/*String*/ input) {
	/*DivideState current */=/* new DivideState(input)*/;
	while (/*true)*/
	{
	/*final Option<Character> pop */=/* current.pop()*/;
	/*if */(/*pop instanceof None<Character>) break*/;
	/*if (pop instanceof Some<Character>(Character c)) current */=/* folder.fold(current, c)*/;}
	/*return current.advance*/(/*).stream()*/;
}
/*@Override
	public String*/ delimiter_FoldingDivider() {
	/*return folder.delimiter*/(/*)*/;
}
