struct Rectangle {
    int32_t width;
    int32_t height;
};

struct Point {
    int32_t x;
    int32_t y;
};

int32_t manhattan_Point(struct Point* this) {
    return this->x + this->y;
}

struct Point Point(int32_t x, int32_t y) {
    struct Point this;
    this.x = x;
    this.y = y;
    return this;
}

int32_t calculateArea(int32_t width, int32_t height) {
    return width * height;
}

struct Point point1 = Point(3, 4);

int32_t distance = manhattan_Point(&point1);

int32_t area = calculateArea(10, 20);