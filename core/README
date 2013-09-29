What's this?
------------

This is a small little Java library that lets you evaluate arbitrary mathematical expressions
in string form. I may or may not have ~~stolen the idea~~ "gained inspiration" from
[the BitSquid engine's blog](http://bitsquid.blogspot.com/2011/03/putting-some-of-power-of-programming.html).

How?
----

To find 2+2*4:

```java
Expression exp = Expression.parse("2+2*4");
System.out.println( exp.evaluate() ); //should print 10
```

You can use variables and functions; just remember that variables always start with a
lowercase letter, and functions always start with an uppercase:

```java
Expression exp = Expression.parse("Abs(x)");

/* now that we have variables in the expression, we have to
 * provide a map so that Expression.evaluate() can map
 * var names -> values.
 */
Map<String, Double> variables = new HashMap<>();
variables.put("x", -30.5);

System.out.println( exp.evaluate(variables) ); //should print 30.5
```

The constants E and PI are supported; they are just variables that are implicitly
included for all expressions:

```java
Expression exp = Expression.parse("pi");
System.out.println( exp.evaluate() ); //prints 3.14blahblahblah
```

Note that we only need to provide a Map to Expression.evaluate() if the expression
includes variables.

Also, since Expression implements Serializable, you can read/write it with
ObjectInputStream/ObjectOutputStream.

Why?
----

No reason, really, it just looked like something that would be fun to implement.

No, really, how?
----------------

Most of the magic happens in the Compiler class; first, the expression is run through
a lexer and turned into a list of tokens. Then, the token list is handed to a parser.
The parser converts the token list to reverse polish notation using the
[Shunting-Yard Algorithm](https://en.wikipedia.org/wiki/Shunting_yard_algorithm).
The RPN-ed expression is then stored in an Expression object.

A simple stack VM is then used by Expression to evaluate the expression.

Is it done?
-----------

No.

All of the basic functionality is there, but there are some small things
that I still want to do:

* Finish the 2nd semantic checker pass in the Compiler
* Fix documentation & comments
* Add support for simplifying expressions (ex: turn "4*4*Sin(x)" into "16*Sin(x)")