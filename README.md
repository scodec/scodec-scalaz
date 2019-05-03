scodec-scalaz
=============

Overview
--------

This library provides scalaz specific extensions to scodec-core and scodec-bits. It provides typeclass instances for many data types (e.g., `Monoid[BitVector]`, `Monad[Attempt]`, `InvariantFunctor[Codec]`) and syntax for converting between `scodec.Attempt` and `scalaz.\/`.

All typeclass instances and syntax extensions are defined in the `scodec.interop.scalaz` package, so usage looks like:

```scala
import scodec.interop.scalaz._

myCodec.decode(bitVector).toDisjunction
```

Getting Binaries
----------------

See the [releases page on the website](http://scodec.org/releases/).

Administrative
--------------

This project is licensed under a [3-clause BSD license](LICENSE).

The [scodec channel on Gitter](https://gitter.im/scodec/scodec) is a good place to go for help. Also consider using the [scodec tag on StackOverflow](http://stackoverflow.com/questions/tagged/scodec).

Code of Conduct
---------------

See the [Code of Conduct](CODE_OF_CONDUCT.md).

