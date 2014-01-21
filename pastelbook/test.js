/**
 * Created by KIMSEONHO on 14. 1. 15.
 */
function add(x, y) {
    console.dir(arguments.callee);
    return x + y;
}

add.result = add(3, 2);
add.status = 'OK';

console.log(add.result);
console.log(add.status);

function myFunction() {
    return true;
}

function parent() {
    var a = 100;

    var child = function() {
        console.log(a);
        a++;
    }

    return child;
}

var inner = parent();
inner();

function Person(name) {
    this.name = name;
}