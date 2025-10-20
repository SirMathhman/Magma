template <typeparam T>
struct Array {
	T* ref;
	int capacity;
	int length;
};
template <typeparam T>
struct ArrayList {
	Array<T> elements;
};
struct Collections {
};
