package phi

/**
 * Created by pony on 15/04/30.
 */

object Phi extends ListAlgebra {

  def id[A]: A => A =
    a => a

  def const[A, B]: A => B => A =
    a => _ => a

  def fst[A, B]: ((A, B)) => A = {
    case (x, _) => x
  }

  def snd[A, B]: ((A, B)) => B = {
    case (_, y) => y
  }

  def fork[A, B, C]: (A => B) => (A => C) => A => (B, C) =
    f => g => x => (f(x), g(x))

  case class :*:[+A, +B](a: A, b: B)

  def prod[A, B, C]: ((A, B) => C) => A :*: B => C =
    f => {
      case a :*: b => f(a, b)
    }

  implicit class ProdCons[A](b: A) {
    def :*:[B](a: B): B :*: A = Phi.:*:(a, b)
  }

  implicit class ProdFunction[A, B](f: A => B) {
    def /\[C]: (A => C) => A => B :*: C =
      g => x => f(x) :*: g(x)

    def *|*[C, D]: (C => D) => A :*: C => B :*: D =
      g => {
        case a :*: b => f(a) :*: g(b)
      }
  }

  trait :+:[+A, +B]

  case class Inl[A](a: A) extends :+:[A, Nothing]

  case class Inr[B](b: B) extends :+:[Nothing, B]

  def inl[A, B](a: A) = Inl(a)

  def inr[A, B](b: B) = Inr(b)

  implicit class CoProdFunction[A, C](f: A => C) {
    def \/[B]: (B => C) => A :+: B => C =
      g => {
        case Inl(a) => f(a)
        case Inr(b) => g(b)
      }

    def +|+[B, D]: (B => D) => A :+: B => C :+: D =
      g => {
        case Inl(a) => inl[C, D](f(a))
        case Inr(b) => inr[C, D](g(b))
      }
  }

  trait FAlgebra[F[_, _], T[_], A] {
    def fmap[B, C]: (B => C) => F[A, B] => F[A, C]

    def hylo[G[_, _], B](φ: G[A, B] => B)(η: F[A, B] => G[A, B])(ψ: A => F[A, A]): A => B =
        a => φ(η(fmap(hylo(φ)(η)(ψ))(ψ(a))))
  }

  trait InitialAlgebra[F[_, _], T[_], A] extends FAlgebra[F, T, A] {
    def out: T[A] => F[A, T[A]]

    def cata[B](φ: F[A, B] => B): T[A] => B =
      t => φ(fmap(cata(φ))(out(t)))
  }

  trait FinalCoalgebra[F[_, _], T[_], A] extends FAlgebra[F, T, A] {
    def inn: F[A, T[A]] => T[A]

    def ana[B](ψ: B => F[A, B]): B => T[A] =
      b => inn(fmap(ana(ψ))(ψ(b)))
  }

  def cata[F[_, _], T[_], A, B](φ: F[A, B] => B)(implicit ia: InitialAlgebra[F, T, A]): T[A] => B =
    ia.cata(φ)

  def cataT[F[_, _], T[_], A, G[_, _], U[_], B](τ: (G[B, U[B]] => U[B]) => F[A, U[B]] => U[B])(implicit ia: InitialAlgebra[F, T, A], fc: FinalCoalgebra[G, U, B]): T[A] => U[B] =
    ia.cata(τ(fc.inn))

  def ana[F[_, _], T[_], A, B](ψ: B => F[A, B])(implicit fc: FinalCoalgebra[F, T, A]): B => T[A] =
    fc.ana(ψ)

  def anaS[F[_, _], T[_], A, G[_, _], U[_], B](σ: (U[B] => G[B, U[B]]) => U[B] => F[A, U[B]])(implicit ia: InitialAlgebra[G, U, B], fc: FinalCoalgebra[F, T, A]): U[B] => T[A] =
    fc.ana(σ(ia.out))


  //  def hyloEST[A, B, F, G](τ: (FAlgebra[G, G] => G) => FAlgebra[G, B] => B)(η: FAlgebra[F, B] => FAlgebra[G, B])(σ: (F => FAlgebra[F, F]) => A => FAlgebra[F, A])(implicit ia: InitialAlgebra[F], fc: FinalCoalgebra[G]): A => B =
//    hylo[A, B, F, G](τ(fc.inn))(η)(σ(ia.out))
//
//  def hyloEta[F, G](η: FAlgebra[F, G] => FAlgebra[G, G])(implicit ia: InitialAlgebra[F], fc: FinalCoalgebra[G]): F => G =
//    hyloEST[F, G, F, G](id)(η)(id)
//
//  def hyloSigma[A, B, F, G](φ: FAlgebra[G, B] => B)(η: FAlgebra[F, B] => FAlgebra[G, B])(σ: (F => FAlgebra[F, F]) => A => FAlgebra[F, A])(implicit ia: InitialAlgebra[F]): A => B =
//    hylo[A, B, F, G](φ)(η)(σ(ia.out))
//
//  def hyloTau[A, B, F, G](τ: (FAlgebra[G, G] => G) => FAlgebra[G, B] => B)(η: FAlgebra[F, B] => FAlgebra[G, B])(ψ: A => FAlgebra[F, A])(implicit fc: FinalCoalgebra[G]): A => B =
//    hylo[A, B, F, G](τ(fc.inn))(η)(ψ)

}