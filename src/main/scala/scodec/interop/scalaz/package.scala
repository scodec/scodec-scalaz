package scodec.interop

import language.higherKinds

import _root_.scalaz._

import scodec.bits.{ BitVector, ByteVector }
import scodec._

/** Provides interop between scodec-core and scalaz. */
package object scalaz extends scodec.interop.scalaz.ScalazInstancesLowPriority {

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
  implicit val BitVectorShowInstance: Show[BitVector] = Show.showFromToString
  implicit val BitVectorEqualInstance: Equal[BitVector] = Equal.equalA

  implicit val ByteVectorMonoidInstance: Monoid[ByteVector] = Monoid.instance(_ ++ _, ByteVector.empty)
  implicit val ByteVectorShowInstance: Show[ByteVector] = Show.showFromToString
  implicit val ByteVectorEqualInstance: Equal[ByteVector] = Equal.equalA

  implicit val AttemptMonad: Monad[Attempt] = new Monad[Attempt] {
    def point[A](a: => A) = Attempt.successful(a)
    def bind[A, B](fa: Attempt[A])(f: A => Attempt[B]) = fa flatMap f
  }
  implicit def AttemptShowInstance[A]: Show[Attempt[A]] = Show.showFromToString
  implicit def AttemptEqualInstance[A]: Equal[Attempt[A]] = Equal.equalA

  implicit val DecodeResultTraverseComonadInstance: Traverse[DecodeResult] with Comonad[DecodeResult] = new Traverse[DecodeResult] with Comonad[DecodeResult] {
    def copoint[A](fa: DecodeResult[A]) = fa.value
    def cobind[A, B](fa: DecodeResult[A])(f: DecodeResult[A] => B) = DecodeResult(f(fa), fa.remainder)
    override def map[A, B](fa: DecodeResult[A])(f: A => B) = fa map f
    def traverseImpl[G[_], A, B](fa: DecodeResult[A])(f: A => G[B])(implicit G: Applicative[G]) = G.map(f(fa.value)) { b => DecodeResult(b, fa.remainder) }
  }
  implicit def DecodeResultShowInstance[A]: Show[DecodeResult[A]] = Show.showFromToString[DecodeResult[A]]
  implicit def DecodeResultEqualInstance[A]: Equal[DecodeResult[A]] = Equal.equalA

  implicit val DecoderMonadInstance: Monad[Decoder] = new Monad[Decoder] {
    def point[A](a: => A) = Decoder.point(a)
    def bind[A, B](fa: Decoder[A])(f: A => Decoder[B]) = fa flatMap f
  }
  implicit def DecoderMonoidInstance[A](implicit A: Monoid[A]): Monoid[Decoder[A]] = new DecoderSemigroup[A]() with Monoid[Decoder[A]] {
    def zero = Decoder.point(A.zero)
  }
  implicit def DecoderShowInstance[A]: Show[Decoder[A]] = Show.showFromToString[Decoder[A]]

  implicit val EncoderCovariantInstance: Contravariant[Encoder] = new Contravariant[Encoder] {
    def contramap[A, B](fa: Encoder[A])(f: B => A) = fa contramap f
  }
  implicit val EncoderCorepresentableAttemptInstance: Corepresentable[Encoder, Attempt[BitVector]] = new Corepresentable[Encoder, Attempt[BitVector]] {
    def corep[A](f: A => Attempt[BitVector]): Encoder[A] = Encoder(f)
    def uncorep[A](f: Encoder[A]): A => Attempt[BitVector] = a => f.encode(a)
  }
  implicit def EncoderShowInstance[A]: Show[Encoder[A]] = Show.showFromToString[Encoder[A]]

  implicit val GenCodecProfunctorInstance: Profunctor[GenCodec] = new Profunctor[GenCodec] {
    def mapfst[A, B, C](fab: GenCodec[A, B])(f: C => A): GenCodec[C, B] = fab.contramap(f)
    def mapsnd[A, B, C](fab: GenCodec[A, B])(f: B => C): GenCodec[A, C] = fab.map(f)
  }
  implicit def GenCodecShowInstance[A, B]: Show[GenCodec[A, B]] = Show.showFromToString[GenCodec[A, B]]

  implicit val CodecInvariantFunctorInstance: InvariantFunctor[Codec] = new InvariantFunctor[Codec] {
    def xmap[A, B](fa: Codec[A], f: A => B, g: B => A): Codec[B] = fa.xmap(f, g)
  }
  implicit def CodecShowInstance[A]: Show[Codec[A]] = Show.showFromToString[Codec[A]]
}
