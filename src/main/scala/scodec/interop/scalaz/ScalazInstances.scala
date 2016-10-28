package scodec.interop.scalaz

import _root_.scalaz._

import scodec.bits.BitVector
import scodec._

private[scalaz] abstract class ScalazInstancesLowPriority {

  implicit final def DecoderSemigroupInstance[A](implicit A: Semigroup[A]): Semigroup[Decoder[A]] =
    new DecoderSemigroup[A]()

}

private class DecoderSemigroup[A](implicit A: Semigroup[A]) extends Semigroup[Decoder[A]] {
  def append(x: Decoder[A], y: => Decoder[A]) = new Decoder[A] {
    private lazy val yy = y
    def decode(bits: BitVector) = (for {
      first <- x
      second <- yy
    } yield A.append(first, second)).decode(bits)
  }
}
