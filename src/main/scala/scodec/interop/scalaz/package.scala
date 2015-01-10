package scodec.interop

import _root_.scalaz._

import scodec.bits.{ BitVector, ByteVector }
import scodec._

/** Provides interop between scodec-core and scalaz. */
package object scalaz {

  /** Extension methods for an `Err \/ A`. */
  implicit class ErrDisjunctionSyntax[A](val self: Err \/ A) extends AnyVal {
    /** Converts this disjunction to an attempt. */
    def toAttempt: Attempt[A] = self.fold(Attempt.failure, Attempt.successful)
  }

  /** Extension methods for an `Attempt[A]`. */
  implicit class AttemptSyntax[A](val self: Attempt[A]) extends AnyVal {
    /** Converts this attempt to a disjunction. */
    def toDisjunction: Err \/ A = self.fold(\/.left, \/.right)
  }

  /** Extension methods for a `Codec[A]`. */
  implicit class CodecSyntax[A](val self: Codec[A]) extends AnyVal {

    /**
     * Converts this to a `Codec[Unit]` that encodes using the zero value of the implicitly
     * available `Monoid[A]` and decodes a unit value when this codec decodes an `A` successfully.
     *
     * @group combinators
     */
    def unitM(implicit m: Monoid[A]): Codec[Unit] = self.unit(m.zero)
  }

  implicit val BitVectorMonoidInstance: Monoid[BitVector] = Monoid.instance(_ ++ _, BitVector.empty)

  implicit val ByteVectorMonoidInstance: Monoid[ByteVector] = Monoid.instance(_ ++ _, ByteVector.empty)

  implicit val AttemptMonad: Monad[Attempt] = new Monad[Attempt] {
    def point[A](a: => A) = Attempt.successful(a)
    def bind[A, B](fa: Attempt[A])(f: A => Attempt[B]) = fa flatMap f
  }

  implicit val DecoderMonadInstance: Monad[Decoder] = new Monad[Decoder] {
    def point[A](a: => A) = Decoder.point(a)
    def bind[A, B](fa: Decoder[A])(f: A => Decoder[B]) = fa flatMap f
  }

  implicit def DecoderMonoidInstance[A](implicit A: Monoid[A]): Monoid[Decoder[A]] = new Monoid[Decoder[A]] {
    def zero = Decoder.point(A.zero)
    def append(x: Decoder[A], y: => Decoder[A]) = new Decoder[A] {
      private lazy val yy = y
      def decode(bits: BitVector) = (for {
        first <- DecodingContext(x)
        second <- DecodingContext(yy)
      } yield A.append(first, second)).decode(bits)
    }
  }

  implicit val EncoderCovariantInstance: Contravariant[Encoder] = new Contravariant[Encoder] {
    def contramap[A, B](fa: Encoder[A])(f: B => A) = fa contramap f
  }

  implicit val EncoderCorepresentableAttemptInstance: Corepresentable[Encoder, Attempt[BitVector]] = new Corepresentable[Encoder, Attempt[BitVector]] {
    def corep[A](f: A => Attempt[BitVector]): Encoder[A] = Encoder(f)
    def uncorep[A](f: Encoder[A]): A => Attempt[BitVector] = a => f.encode(a)
  }

  implicit val GenCodecProfunctorInstance: Profunctor[GenCodec] = new Profunctor[GenCodec] {
    def mapfst[A, B, C](fab: GenCodec[A, B])(f: C => A): GenCodec[C, B] = fab.contramap(f)
    def mapsnd[A, B, C](fab: GenCodec[A, B])(f: B => C): GenCodec[A, C] = fab.map(f)
  }

  implicit val CodecInvariantFunctorInstance: InvariantFunctor[Codec] = new InvariantFunctor[Codec] {
    def xmap[A, B](fa: Codec[A], f: A => B, g: B => A): Codec[B] = fa.xmap(f, g)
  }
}
