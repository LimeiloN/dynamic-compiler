### dynamic-compiler
A nice and easy API to add more complexity and mess in your program.

#### What can it do
It can turn your mess in a more readable mess (for the jvm at least).
Wait, it can also handle multiple classes (like from multiple files), 
but also run and eval mess on the fly (hehe). 

So now you can compile code that compile code [https://www.youtube.com/watch?v=9CS7j5I6aOc], 
or create your own (shitty) jshell. A more cool use case would be to parse 
math expressions (rly it's nice).

#### Flawless
This whole project is a security flaw. If you make the user enter code to 
be run, PLS make sure to sanitize the input or expect a Java Injection.

#### Get it
Search on Jitpack by yourself.

#### Credits
Consider this a fork of [https://github.com/raulgomis/dynamic-java-compiler] © Raúl Gomis. As I clearly started from his code (my bad).
Also freely inspired from [https://github.com/michaelliao/compiler] © Michael Liao.