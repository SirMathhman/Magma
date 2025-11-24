template <typename T>
struct Wrapper {
	T value;
};

struct Value {
	int data;
};

int main() {
	Value data = {42};
	Wrapper<Value> wrappedData = {data};

	return 0;
}