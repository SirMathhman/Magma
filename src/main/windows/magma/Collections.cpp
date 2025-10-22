// File generated from '.\src\main\java\magma\Collections.java'. This is not source code!
#include "Main.h"
template <typename T>
private Array_Array(void* _ref, T* ref, int capacity){
	this.ref = ref;
	this.capacity = capacity;
	this.length = 0;
}
template <typename T>
Array<T> alloc_Array(void* _ref, int length){
	return new_Array<T>(MemUtils.malloc(length), length);
}
template <typename T>
void copyTo_Array(void* _ref, int srcPos, Array<T> dest, int destPos, int length){
	System.arraycopy(this.ref, srcPos, dest.ref, destPos, length);
}
auto __lambda0__(auto element) {
	return Objects.equals(element, test);
}
template <typename T>
bool contains_Array(void* _ref, T test){
	Stream < T >= tStream == this.stream();
	return tStream.collect(new_AnyMatch<T>(__lambda0__));
}
template <typename T>
Stream<T> stream_Array(void* _ref){
	return new_Stream<T>(new_ArrayHead<T>(this.ref, this.length));
}
template <typename T>
void setNext_Array(void* _ref, T element){
	if (this.length < this.ref.length) {
		/*this.ref[this.length] */ = element;
		this.length++;
	}
}
template <typename T>
Option<T> get_Array(void* _ref, int index){
	if (index < this.length) {
		return new_Some<T>(/*this.ref[index]*/);
	}
	else {
		return new_None<T>();
	}
}
template <typename T>
void setFirst_Array(void* _ref, T element){
	/*this.ref[0] */ = element;
}
template <typename T>
private ArrayList_ArrayList(void* _ref, Array<T> elements){
	this.elements = elements;
}
template <typename T>
ArrayList new_ArrayList(void* _ref){
	ArrayList this;
	this(Array.alloc(10));
	return this;
}
template <typename T>
ArrayList<T> from_ArrayList(void* _ref, /*T...*/ elements){
	return Streams.fromRef(elements).collect(new_ListCollector<T>());
}
template <typename T>
void ensureCapacity_ArrayList(void* _ref, int minCapacity){
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
template <typename T>
ArrayList<T> addLast_ArrayList(void* _ref, T element){
	this.ensureCapacity((this.elements).length + 1);
	this.elements.setNext(element);
	return this;
}
template <typename T>
ArrayList<T> clear_ArrayList(void* _ref){
	this.elements == Array.alloc(10);
	return this;
}
template <typename T>
int size_ArrayList(void* _ref){
	return (this.elements).length;
}
template <typename T>
Stream<T> stream_ArrayList(void* _ref){
	return new_Stream<T>(new_ListHead<T>(this));
}
template <typename T>
Option<T> get_ArrayList(void* _ref, int index){
	if (index < 0 || index >= (this.elements).length) {
		return new_None<T>();
	}
	return this.elements.get(index);
}
template <typename T>
bool isEmpty_ArrayList(void* _ref){
	return this.size() == 0;
}
template <typename T>
ArrayList<T> addFirst_ArrayList(void* _ref, T element){
	this.ensureCapacity((this.elements).length + 1);
	/*// Shift all elements one position to the right
			this.elements.copyTo(0, this.elements, 1, (this.elements).length)*/;
	this.elements.setFirst(element);
	this.elements.length++;
	return this;
}
template <typename T>
bool contains_ArrayList(void* _ref, T element){
	return this.elements.contains(element);
}
template <typename T>
Option<T> getFirst_ArrayList(void* _ref){
	return this.get(0);
}
template <typename T>
Option<ArrayList<T>> subList_ArrayList(void* _ref, int start, int end){
	if (start < 0 || end >= (this.elements).length || start >= end) {
		return new_None<ArrayList<T>>();
	}
	int subSize = end - start;
	Array < T >= newElements == Array.alloc(Math.max(10, subSize));
	this.elements.copyTo(start, newElements, 0, subSize);
	newElements.length = subSize;
	return new_Some<ArrayList<T>>(new_ArrayList<T>(newElements));
}
template <typename T>
ArrayList<T> addAllLast_ArrayList(void* _ref, ArrayList<T> elements){
	int elementsSize = elements.size();
	this.ensureCapacity((this.elements).length + elementsSize);
	/*for (int i = 0; i < elementsSize; i++) {
				this.elements.setNext(elements.get(i).orElse(null));
			}*/
	return this;
}
template <typename T>
Option<ArrayList<T>> addAllAt_ArrayList(void* _ref, int index, ArrayList<T> elements){
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
template <typename T>
Option<T> getLast_ArrayList(void* _ref){
	return this.get((this.elements).length - 1);
}
template <typename T>
ArrayList<T> copy_ArrayList(void* _ref){
	ArrayList<T> list = new_ArrayList<T>();
	list.ensureCapacity(this.elements.length);
	if ((this.elements).length >= 0) {
		this.elements.copyTo(0, list.elements, 0, (this.elements).length);
		list.elements.length = (this.elements).length;
	}
	return list;
}
auto __lambda1__(auto ttTuple) {
	return ttTuple.left().equals(ttTuple.right());
}
template <typename T>
bool equalsTo_ArrayList(void* _ref, ArrayList<T> other){
	if (this.size() == other.size()) {
		return this.stream().zip(other.stream()).collect(new_AllMatch<Tuple<T, T>>(__lambda1__));
	}
	return false;
}
template <typename T>
ArrayList<T> removeValue_ArrayList(void* _ref, T element){
	/*for (int i = 0; i < this.elements.length; i++) {
				Option<T> current = this.elements.get(i);
				if (current instanceof Some<T>(T value) && Objects.equals(value, element)) {
					// Shift elements left to fill the gap
					this.elements.copyTo(i + 1, this.elements, i, this.elements.length - i - 1);
					this.elements.length--;
					break;
				}
			}*/
	return this;
}
int main(){
	main_Main();
	return 0;
}