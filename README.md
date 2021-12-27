[![Build Status](https://img.shields.io/travis/com/rallyhealth/scalacheck-ops)](https://app.travis-ci.com/github/rallyhealth/scalacheck-ops)
[![CodeCov](https://img.shields.io/codecov/c/github/rallyhealth/scalacheck-ops)](https://codecov.io/gh/rallyhealth/scalacheck-ops)

| scalacheck-ops_1-12 | scalacheck-ops_1-13 | scalacheck-ops_1-14 | scalacheck-ops_1-15 |
| :-----------------: | :-----------------: | :-----------------: | :-----------------: | 
| ![Download for ScalaCheck 1.12 & Scala 2.11](https://maven-badges.herokuapp.com/maven-central/com.rallyhealth/scalacheck-ops_1-12_2.11/badge.svg?style=flat) | ![Download for ScalaCheck 1.13 & Scala 2.12](https://maven-badges.herokuapp.com/maven-central/com.rallyhealth/scalacheck-ops_1-13_2.12/badge.svg?style=flat) | ![Download for ScalaCheck 1.14 & Scala 2.13](https://maven-badges.herokuapp.com/maven-central/com.rallyhealth/scalacheck-ops_1-14_2.13/badge.svg?style=flat) | ![Download for ScalaCheck 1.15 & Scala 2.13](https://maven-badges.herokuapp.com/maven-central/com.rallyhealth/scalacheck-ops_1-15_2.13/badge.svg?style=flat) |

# Summary

A library that provides [ScalaCheck](https://www.scalacheck.org/) implicits and helper 
methods made available via:
```scala
import org.scalacheck.ops._
```

See the [use cases section](#use-cases) for use cases and operations that are enabled 
with scalacheck-ops.

# Installation

### Compatibility

**NOTE** Version 1.x lives under the `"me.jeffmay"` organization. Version 2.x lives
under `"com.rallyhealth"`

**NOTE** Version 2.x and above **requires** JDK >=8 and Scala >=2.11 as this 
library expects the java.time standard library module.

**NOTE** As of `scalacheck-ops` >=2.0, the version of scalacheck is always
included in the artifact name. Prior to this change, `scalacheck-ops` with
no version suffix would pull in ScalaCheck version 1.12.6. 

| Artifact Name       | Version Limit  | ScalaCheck | Supported JDK | Supported Scala  |
| :-----------------: | :------------: | :--------: | :-----------: | :--------------: |
| scalacheck-ops_1-15 | x >= 2.5.2     |     1.15.4 | 8             | 2.12, 2.13, 3    |
| scalacheck-ops_1-14 | x >= 2.0       |     1.14.3 | 8             | 2.11, 2.12, 2.13 |
| scalacheck-ops_1-13 | x >= 2.0       |     1.13.5 | 8             | 2.11, 2.12       |
| scalacheck-ops_1-13 | 1.5 <= x < 2.0 |     1.13.4 | 6 - 8         | 2.10 - 2.11      |
| scalacheck-ops_1-12 | x >= 2.0       |     1.12.6 | 8             | 2.11             |
| scalacheck-ops      | x < 2.0        |     1.12.6 | 6 - 8         | 2.10 - 2.11      |

The same source code is compiled against specific versions of Scala and ScalaCheck. 
We use separate artifacts to avoid causing issues with transitive dependencies on
ScalaCheck being evicted with binary incompatible versions.

### ScalaTest Compatibility

Specifically, when using this library with ScalaTest you might notice the 
following exception:

```
java.lang.IncompatibleClassChangeError: Found class org.scalacheck.Gen, but interface was expected
```

This is because you need `scalacheck-ops_1-13` for ScalaTest 3.0.x

| ScalaTest Version | ScalaCheck Version |
| :---------------: | :----------------: |
|             2.2.x |             1.12.6 |
|             3.0.x |             1.13.4 |

# Use Cases

## Converting Gen to Iterator

Probably one of the most used features of this library is when you want 
to use generators for testing properties as well as testing single case 
unit tests. In plain ScalaCheck, you are able to call the `Gen.sample` 
method to get an `Option[T]` from a `Gen[T]`. However, for single unit 
tests, you need a `T`, so you could call `.get` on the option, but this 
option could be empty if the sample is filtered. ScalaCheck handles this
internally by limiting the number of attempts, but it does not expose
this logic for the developer. This library provides a safe way (and some
unsafe ways) to get values out of a `Gen`:

```scala
import org.scalacheck.Gen
import org.scalacheck.ops._

val genEvens = Gen.choose(1, 10).suchThat(_ % 2 == 0)
val exampleEven = genEvens.getOrThrow
```

By default, `.getOrThrow` will make 100 attempts to get a value out of 
the generator before giving up. You can lower or raise this amount with:
```scala
val exampleEven = genEvens.getOrThrow(10)
```

In addition to getting a single value, you can convert a `Gen` into an
`Iterator` or `Iterable`:
```scala
val evens = genEvens.toIterable
```

By default, this iterable attempts a max of 100 times for each sample
before giving up and throwing an exception. This is the safer default,
however, if you are willing to risk an infinite loop (because you are
not worried about your filters blocking indefinitely), you can use:
```scala
val evens = genEvens.toUnboundedIterator
```

But if your filters are lenient enough, then this should almost always
be the same as calling `.toIterator` but the choice is yours.

## Generating Sets

One thing that is missing from ScalaCheck is a performant way to 
generate `Set`s of an arbitrary or specific size. The naive
implementation is insufficient:
```scala
val genSetOf3 = Gen.listOfN(3, Gen.oneOf(0 to 10)).map(_.toSet[Int])
```

This may work in some of the early and simple cases, but when you run
it long enough, you will find sets of size 1 and 2. This is because
`Gen.oneOf` uses a psuedo-random number generator, which will on 
occasion have duplicates. When you call `.toSet` it will filter out the
duplicates and leave you with a smaller `Set` than you wanted.

In a similar manner to generating iterators mentioned above, this 
library gives you a method to generate a specific sized `Set` (or throw
an exception after a predefined number of tries).
```scala
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalacheck.ops._
val genSetOf3 = Gen.setOfN(3, Gen.oneOf(0 to 10))
// or give up on building the Set after 10 duplicates
val genSetOf3 = Gen.setOfN(3, 10, Gen.oneOf(0 to 10))
```

## Generating Strings a specific size

Let's say you have a password validator that requires strings with a
length of 20 characters. If you want to generate only valid passwords,
then you could generate strings of length 20.
```scala
// vanilla scalacheck
import org.scalacheck.Gen
import org.scalacheck.Arbitrary.arbitrary
val genValidPasswords = for {
  cs <- Gen.collectionOfN[Array](20, arbitrary[Char])
} yield new String(cs, "utf-8")

// scalacheck-ops
import org.scalacheck.Gen
import org.scalacheck.ops._
val genValidPasswords = Gen.stringOfN(20)
```

You could even generate strings within a certain range of characters:
```scala
// vanilla scalacheck
import org.scalacheck.Gen
import org.scalacheck.Arbitrary.arbitrary
val genValidPasswords = for {
  n <- Gen.choose(20, 40)
  cs <- Gen.collectionOfN[Array](n, arbitrary[Char])
} yield new String(cs, "utf-8")

// scalacheck-ops
import org.scalacheck.Gen
import org.scalacheck.ops._
val genValidPasswords = Gen.stringOfNWithin(20 to 40)
```

## Generating Enums, BitSets, and Binary

Some useful generators that were missing from the vanilla ScalaCheck:

```scala
import org.scalacheck.Gen
import org.scalacheck.ops._

object Colors extends scala.Enumeration {
  val Red, Blue, Green = Value
}

Gen.boolean // Gen[Boolean]
Gen.bits // Gen[BitSet]
Gen.enumValue(Colors) // Gen[Colors.Value]
```
