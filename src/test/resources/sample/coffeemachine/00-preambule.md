
<p style="text-align: center;">
<img src="/customer.jpeg"/>
</p>


Preamble
========

In this Coffe Machine Project, your task is to implement the logic (starting
from a simple class) that translates orders from customers of the coffee
machine to the drink maker. Your code will use the drink maker protocol to
send commands to the drink maker.

<p style="text-align: center;">
   <img src="/coffee-800x700.png" width="200px"/>
</p>


**Important !**


> You do not have to implement the coffee machine customer interface. For
> instance, your code could consume a simple POJO that would represent an
> order from a customer.
>
> You do not have to implement the drink maker. It is only a imaginery
> engine that will receive messages according to the protocol. Your job
> is to build those messages.

Iterations
----------

This project starts simple and will grow in added features through the iterations.


 1. First iteration: Making Drinks ( *~30minutes* )
 2. Second iteration: Going into business ( *~20minutes* )
 3. Third iteration: Extra hot ( *~20minutes* )
 4. Fourth iteration: Making money ( *~20minutes* )
 5. Fifth iteration: Running out ( *~20minutes* )


{% asciidiag %}

 +---------+--------+------+------+-----+-----+-----+
 |         |  {o}   | {mo} | {io} | {c} | {s} | {d} |
 |    {tr} |        |      |      |     |     |     |
 +---------+--------+------+--=---+-----+-----+-----+
{% asciidiag %}


Ready ?


**Requirements**

 * Your favorite IDE or text editor
 * A testing framework (`junit`, `rspec`, ...)
 * A mocking framework (`mockito`, ...)
 * A passion for tested code ;)


{% asciidiag %}
                                           |
          +----------+        +---------+  |
          |     Mail |<-\     | Stock   |  |
          | cPNK {d} |  | /---| cPNK {s}|  |
          +----------+  : :   +---------+  |
                        | v                |
  /---------+     +------------+           |   +---------+
  |  Order  |---->|  Protocol  |-------------->| Drink   |
  |    cBLU |     |     {io}   |---\       |   | cGRE    |
  +---------/     +------------+   |       |   +---------+
                                   \-=-------->| Message |
                                           |   | cRED    |
                                           |   +---------+
                                           |
{% asciidiag %}
