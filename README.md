scodec-scalaz
=============

Overview
--------

This library provides scalaz specific extensions to scodec-core and scodec-bits. It provides typeclass instances for many data types (e.g., `Monoid[BitVector]`, `Monad[Attempt]`, `InvariantFunctor[Codec]`) and syntax for converting between `scodec.Attempt` and `scalaz.\/`.

All typeclass instances are syntax extensions are defined in the `scodec.interop.scalaz` package, so usage looks like:

```scala
import scodec.interop.scalaz._

myCodec.decode(bitVector).toDisjunction
```

Getting Binaries
----------------

See the [releases page on the wiki](https://github.com/scodec/scodec/wiki/Releases).

Administrative
--------------

This project is licensed under a [3-clause BSD license](LICENSE).

The [Typelevel mailing list](https://groups.google.com/forum/#!forum/typelevel) contains release announcements and is generally a good place to go for help. Also consider using the [scodec tag on StackOverflow](http://stackoverflow.com/questions/tagged/scodec).

People are expected to follow the [Typelevel Code of Conduct](http://typelevel.org/conduct.html)
when discussing scodec on the Github page, IRC channel, mailing list,
or other venues.

Concerns or issues can be sent to Michael Pilquist (*mpilquist@gmail.com*) or
to [Typelevel](http://typelevel.org/about.html).

