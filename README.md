[![Maven Central](https://img.shields.io/maven-central/v/io.github.ali-ghanbari/object-utils.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.ali-ghanbari%22%20AND%20a:%22object-utils%22)

# Object Utilities Library

## Introduction
`object-utils` is yet another reflection-based library
for making arbitrary Java objects serializable, comparable.
The library also provides means of computing distance between
two objects. This library is tested using tens of test cases
which gives us more confidence about the correctness of the
methods provided by it.

One can easily access `object-utils` via Maven Central Repo.,
by adding the following code snippet in the POM file of their
project.

```xml
<dependency>
    <groupId>io.github.ali-ghanbari</groupId>
    <artifactId>object-utils</artifactId>
    <version>LATEST</version>    
</dependency>
```
where `LATEST` denotes the latest version of `object-utils`
shown in the badge above.

## Wrapping Objects
Wrapping objects is the way you can make them serializable
and comparable.

```java
MyClass ref1 = new MyClass(/*...*/);
// ref1 holds the reference to an object of type MyClass
Wrapped w1 = Wrapper.wrapObject(ref1);
```
Easy! Now, you can treat `w` as an object that can be turned
into sequence of bytes and you can invoke `equals` and `hashCode`
methods on it (so it can be used in hash tables).

In order to selectively include fields in the wrapped object,
one can use `InclusionPredicate` to filter out extraneous fields.
The filtering, if implemented efficiently, can result in speeding
up the process of wrapping the object. For example, the following
code snippet can be used to admit only those fields whose name
start with `"employee"`:

```java
MyClass ref2 = new MyClass(/*...*/);
// ref2 holds the reference to an object of type MyClass
Wrapped w2 = Wrapper.wrapObject(ref2, f -> f.getName().startsWith("employee"));
```   

## Unwrapping Objects
Once you are done with serializing and/or comparing a `Wrapped`
object, you can reconstruct the underlying object by simply
calling the method `unwrap` on it. For example, you can use
the following snippet to unwrap `w1` in the above example.

```java
MyClass ref3 = w1.unwrap();
```

Please note that in Java 7, you might need to use a type cast
operator so as to assign the result of `unwrap` to `ref3`.

If you have an object that has some of its fields filtered
out, upon unwrapping the resulting object will have those
fields uninitialized (with default values; please see JVM
specification to see the details). In order to avoid that
we encourage you to pass an already initialized object to
`unwrap` and let the method to reinitialize those fields
that are admitted and leave the rest untouched. We call
such an object a template. For example, in order to unwrap
the object pointed to by `w2` above, you might use the
following code.

```java
MyClass ref4 = new MyClass(/*...*/);
w2.unwrap(ref4);
// only those fields whose name starts with "employee" shall be rewritten 
```

In case of passing templates of unexpected types, the method
shall throw appropriate exception.

## Computing Distance of Wrapped Object
A feature that `object-utils` offers is distance computation.
Given arbitrary Java object, one can compute distance between
the objects. The value returned by the distance is a double
number that indicates how different the two objects are from
each other.

```java
Wrapped w1 = Wrapper.wrapObject("he");
Wrapped w2 = Wrapper.wrapObject("she");
double d = w1.distance(w2); // Levenshtein distance between "he" and "she"
```

## Computing Deep Hashcode
Given arbitrary Java objects, `object-utils` provide functions
for computing hashcode for the objects. This functionality is
available through the static methods `deepHashCode` of the class
`ObjectUtils`. 

```java
int c = ObjectUtils.deepHashCode(new MyClass(/*...*/));
``` 

Similar to the method `wrap`, one can pass an `InclusionPredicate`
object to `deepHashCode` to selectively include the fields in computing
the overall hashcode for the object.

## Related Work
Compared to Google Protobuf, Google GSON, XStream, and Azrael,
 `object-utils` is easier to use and faster. Furthermore,
 this library is more reliable than XStream which failed on
 a number of stress testings!
 
 Compared to Apache Commons Lang, the deep hashcode functionality
 provided by `object-utils` is faster.
 
 ## Credits
 This library is written by [Ali Ghanbari](https://ali-ghanbari.github.io/)
 and is being used in several research projects directly/indirectly related
 to his Ph.D. dissertation.
