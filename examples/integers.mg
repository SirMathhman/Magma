// This is a Magma program to test integer literal support

// Variable declarations with integer literals
var zero = 0;
var one = 1;
var negative = -42;
var large = 1000000;

// Print statements
print "Testing integer literals:";
print zero;
print one;
print negative;
print large;

// Arithmetic with integer literals
print "Arithmetic with integers:";
print 2 + 3;       // Addition
print 10 - 5;      // Subtraction
print 3 * 4;       // Multiplication
print 20 / 5;      // Division

// Mixed arithmetic (integers and floats)
print "Mixed arithmetic:";
print 2 + 3.5;     // Integer + Float
print 10.5 - 5;    // Float - Integer
print 3 * 4.5;     // Integer * Float
print 20 / 2.5;    // Integer / Float

// Comparison with integer literals
print "Comparisons with integers:";
print 5 > 3;       // Greater than
print 5 < 3;       // Less than
print 5 >= 5;      // Greater than or equal
print 5 <= 3;      // Less than or equal
print 5 == 5;      // Equal
print 5 != 3;      // Not equal

// Nested expressions with integer literals
print "Nested expressions:";
print (2 + 3) * 4;
print 2 + (3 * 4);
print (10 - 5) / (2 + 3);

// Integer variables in expressions
print "Expressions with integer variables:";
print zero + one;
print large - negative;
print one * 100;
print large / 1000;