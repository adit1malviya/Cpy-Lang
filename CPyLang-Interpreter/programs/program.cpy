show("=== CPy Final Test ===");

a = 5;
b = 3;

show(a + b);

if (a > b) {
    show("A is greater");
} else {
    show("B is greater");
}

i = 1;
while (i < 4) {
    show(i);
    i = i + 1;
}

func add() {
    return 10 + 20;
}

result = add();
show(result);

arr = [10,20,30,40];

show(arr);

first = arr[0];
last = arr[3];

sum = first + last;

show(sum);

show("Done");