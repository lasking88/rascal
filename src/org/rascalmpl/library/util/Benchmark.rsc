@license{
  Copyright (c) 2009-2015 CWI
  All rights reserved. This program and the accompanying materials
  are made available under the terms of the Eclipse Public License v1.0
  which accompanies this distribution, and is available at
  http://www.eclipse.org/legal/epl-v10.html
}
@contributor{Jurgen J. Vinju - Jurgen.Vinju@cwi.nl - CWI}
@contributor{Paul Klint - Paul.Klint@cwi.nl - CWI}
@contributor{Arnold Lankamp - Arnold.Lankamp@cwi.nl}
@contributor{Davy Landman - Davy.Landman@cwi.nl - CWI}

@doc{
.Synopsis
Functions for time measurement and benchmarking.

.Description

The `Benchmark` library provides the following functions:
loctoc::[1]
}

module util::Benchmark

import Exception;
import IO;

@doc{
.Synopsis
CPU time in nanoseconds (10^-9^ sec).

.Details

.Description

*  Current cpu time in __nanoseconds__ (10^-9^ sec) since the start of the thread that runs the code that calls this function.
*  The cpu time in nanoseconds used by the execution of the code `block`.

.Examples

We use the `fac` function described in link:/Recipes#Basic-Factorial[Factorial] as example:
[source,rascal-shell]
----
import util::Benchmark;
import demo::basic::Factorial;
----
Here we measure time by using separate calls to `cpuTime` before and after a call to `fac`.
[source,rascal-shell,continue]
----
before = cpuTime();
fac(50);
cpuTime() - before;
----
The code to be measured can also be passed as a function parameter to `cpuTime`:
[source,rascal-shell,continue]
----
cpuTime( void() { fac(50); } );
----
These two timings for the same task may differ significantly due to the way these statements are executed here in the tutor.


}

@javaImport{import java.lang.System;}
@javaClass{org.rascalmpl.library.util.Benchmark}
public java int cpuTime();

// Measure the exact running time of a block of code, doc combined with previous function.
public int cpuTime(void () block) {
   int now = cpuTime();
   block();
   return cpuTime() - now;
}

@doc{
.Synopsis
System time in nanoseconds (10^-9^ sec).

.Details

.Description

*  Current system time in nanoseconds (10^-9^ sec) since the start of the thread that runs the code that calls this function.
*  System time in nanoseconds needed to execute the code `block`.

.Examples
We use the `fac` function described in link:/Recipes#Basic-Factorial[Factorial] as example:
[source,rascal-shell]
----
import util::Benchmark;
import demo::basic::Factorial;
----
Here we measure time by using separate calls to `sytemTime` before and after a call to `fac`.
[source,rascal-shell,continue]
----
before = systemTime();
fac(50);
systemTime() - before;
----
The code to be measured can also be passed as a function parameter to `systemTime`:
[source,rascal-shell,continue]
----
systemTime( void() { fac(50); } );
----

}

@javaImport{import java.lang.System;}
@javaClass{org.rascalmpl.library.util.Benchmark}
public java int systemTime();

// Measure the exact running time of a block of code, doc combined with previous function.
public int systemTime(void () block) {
   int now = systemTime();
   block();
   return systemTime() - now;
}

@doc{
.Synopsis
User time in nanoseconds (10^-9^ sec).

.Description

*  Current time in __nanoseconds__ (10^-9^ sec) since the start of the thread that runs the code that calls this function.
*  User time in nanoseconds needed to execute the code `block`.

.Examples

We use the `fac` function described in link:/Recipes#Basic-Factorial[Factorial] as example:
[source,rascal-shell]
----
import util::Benchmark;
import demo::basic::Factorial;
----
Here we measure time by using separate calls to `userTime` before and after a call to `fac`.
[source,rascal-shell,continue]
----
before = userTime();
fac(50);
userTime() - before;
----
The code to be measured can also be passed as a function parameter to `userTime`:
[source,rascal-shell,continue]
----
userTime( void() { fac(50); } );
----
}

@javaImport{import java.lang.System;}
@javaClass{org.rascalmpl.library.util.Benchmark}
public java int userTime();

// Measure the exact running time of a block of code, doc combined with previous function.
public int userTime(void () block) {
   int now = userTime();
   block();
   return userTime() - now;
}

@deprecated{This function can disappear}

@doc{
.Synopsis
Current time in milliseconds (10^-3^ sec).

.Description

*  Current system time in __milliseconds__ (10^-3^ sec) since January 1, 1970 GMT.
*  Real time in milliseconds needed to execute the code `block`.

.Pitfalls
This function is a competitor for the <<DateTime-now>> function that provides a
link:/Rascal#Values-Datetime[datetime] value for the current time.
}

@javaImport{import java.lang.System;}
@javaClass{org.rascalmpl.library.util.Benchmark}
public java int realTime();


// Measure the exact running time of a block of code, doc included in previous function.

public int realTime(void () block) {
   int now = realTime();
   block();
   return realTime() - now;
}

@doc{
.Synopsis
Measure and report the execution time of name:void-closure pairs

.Description

Given is a map that maps strings (used as label to identify each case) to void-closures that execute the code to be benchmarked.
An optional `duration` argument can be used to specify the function to perform the actual measurement. By default the function <<realTime>> is used. A map of labels and durations is returned.

.Examples
We use the `fac` function described in link:/Recipes#Basic-Factorial[Factorial] as example:
[source,rascal-shell]
----
import util::Benchmark;
import demo::basic::Factorial;
----
We measure two calls to the factorial function with arguments `100`, respectively, `200` 
(using by default <<realTime>> that returns milliseconds):
[source,rascal-shell,continue]
----
benchmark( ("fac100" : void() {fac(100);}, "fac200" : void() {fac(200);}) );
----
We can do the same using <<userTime>> that returns nanoseconds:
[source,rascal-shell,continue]
----
benchmark( ("fac100" : void() {fac(100);}, "fac200" : void() {fac(200);}), userTime );
----
}
public map[str,num] benchmark(map[str, void()] Cases) {
	return benchmark(Cases, realTime);
}

public map[str,num] benchmark(map[str, void()] Cases, int (void ()) duration)
{
	measurements = ();
	for (str Name <- Cases) {
		measurements[Name] = duration(Cases[Name]);
	}
	
	return measurements;
}

@doc{
.Synopsis
Current time in nanoseconds (10^-9^ sec) since January 1, 1970 GMT.
.Description

}
@javaClass{org.rascalmpl.library.util.Benchmark}
public java int getNanoTime();

@doc{
.Synopsis
Current time in milliseconds (10^-3^ sec) since January 1, 1970 GMT.

.Description
This function is a synonym for <<realTime>> and gives the wall clock time in milliseconds.
}
@javaClass{org.rascalmpl.library.util.Benchmark}
public java int getMilliTime();

@doc{
.Synopsis
Force a garbage collection.

.Description
This function forces a garbage collection and can, for instance, be used before running a benchmark.
}
@javaClass{org.rascalmpl.library.util.Benchmark}
public java int gc();