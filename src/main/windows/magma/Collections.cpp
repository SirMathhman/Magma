// File generated from 'JavaPath[path=.\src\main\java\magma\Collections.java]'. This is not source code!
#include "Main.h"
private Array_Array(T* ref, int capacity){
	this.ref = ref;
	this.capacity = capacity;
	this.length = 0;
}
Array<T> alloc_Array(int length){
	return new_Array<T>(MemUtils.malloc(length), length);
}
void copyTo_Array(int srcPos, Array<T> dest, int destPos, int length){
	System.arraycopy(this.ref, srcPos, dest.ref, destPos, length);
}
auto __lambda0__(auto element) {
	return Objects.equals(element, test);
}
boolean contains_Array(T test){
	Stream < T >= tStream == this.stream();
	return tStream.collect(new_AnyMatch<T>(__lambda0__));
}
Stream<T> stream_Array(){
	return new_Stream<T>(new_ArrayHead<T>(this.ref, this.length));
}
void setNext_Array(T element){
	if (this.length < this.ref.length) {
		/*this.ref[this.length] */ = element;
		this.length++;
	}
}
Option<T> get_Array(int index){
	if (index < this.length) {
		return new_Some<T>(/*this.ref[index]*/);
	}
	else {
		return new_None<T>();
	}
}
void setFirst_Array(T element){
	/*this.ref[0] */ = element;
}
private ArrayList_ArrayList(Array<T> elements){
	this.elements = elements;
}
ArrayList new_ArrayList(){
	ArrayList this;
	this(Array.alloc(10));
	return this;
}
void ensureCapacity_ArrayList(int minCapacity){
	if (minCapacity <  == this.elements.capacity) {
		/*return*/;
	}
	int newCapacity = /* this.elements.capacity * 2*/;
	if (newCapacity < minCapacity) {
		newCapacity = minCapacity;
	}
	Array < T >= newElements == Array.alloc(newCapacity);
	this.elements.copyTo(0, newElements, 0, (this.elements).length);
	newElements.length = (this.elements).length;
	this.elements = newElements;
}
ArrayList<T> add_ArrayList(T element){
	this.ensureCapacity((this.elements).length + 1);
	this.elements.setNext(element);
	return this;
}
ArrayList<T> clear_ArrayList(){
	this.elements == Array.alloc(10);
	return this;
}
int size_ArrayList(){
	return (this.elements).length;
}
Stream<T> stream_ArrayList(){
	return new_Stream<T>(new_ListHead<T>(this));
}
Option<T> get_ArrayList(int index){
	if (index < 0 || index >= (this.elements).length) {
		return new_None<T>();
	}
	return this.elements.get(index);
}
boolean isEmpty_ArrayList(){
	return this.size() == 0;
}
ArrayList<T> addFirst_ArrayList(T element){
	this.ensureCapacity((this.elements).length + 1);
	/*// Shift all elements one position to the right
			this.elements.copyTo(0, this.elements, 1, (this.elements).length)*/;
	this.elements.setFirst(element);
	this.elements.length++;
	return this;
}
ArrayList<T> addLast_ArrayList(T element){
	return this.add(element);
}
boolean contains_ArrayList(T element){
	return this.elements.contains(element);
}
Option<T> getFirst_ArrayList(){
	return this.get(0);
}
Option<ArrayList<T>> subList_ArrayList(int start, int end){
	if (start < 0 || end >= (this.elements).length || start >= end) {
		return new_None<ArrayList<T>>();
	}
	int subSize = end - start;
	Array < T >= newElements == Array.alloc(Math.max(10, subSize));
	this.elements.copyTo(start, newElements, 0, subSize);
	newElements.length = subSize;
	return new_Some<ArrayList<T>>(new_ArrayList<T>(newElements));
}
ArrayList<T> addAll_ArrayList(ArrayList<T> elements){
	int elementsSize = elements.size();
	this.ensureCapacity((this.elements).length + elementsSize);
	/*for (int i = 0; i < elementsSize; i++) {
				this.elements.setNext(elements.get(i).orElse(null));
			}*/
	return this;
}
Option<ArrayList<T>> addAllAt_ArrayList(int index, ArrayList<T> elements){
	if (index < 0 || index >= (this.elements).length) {
		return new_None<ArrayList<T>>();
	}
	int elementsSize = elements.size();
	this.ensureCapacity((this.elements).length + elementsSize);
	/*// Shift elements to the right to make room
			this.elements.copyTo(index, this.elements, index + elementsSize, (this.elements).length - index)*/;
	/*// Copy inserted elements - need to use set with supplier since we're inserting in middle
			for (int i = 0; i < elementsSize; i++) {
				this.elements.ref[index + i] = elements.get(i).orElse(null);
			}*/
	this.elements.length +  = elementsSize;
	return new_Some<ArrayList<T>>(this);
}
Option<T> getLast_ArrayList(){
	return this.get((this.elements).length - 1);
}
ArrayList<T> copy_ArrayList(){
	ArrayList<T> list = new_ArrayList<T>();
	if ((this.elements).length >= 0) {
		this.elements.copyTo(0, list.elements, 0, (this.elements).length);
		list.elements.length = (this.elements).length;
	}
	return list;
}
int main(){
	main_Main();
	return 0;
}