package phi

/**
 * Created by pony on 15/04/30.
 */

import Phi._

trait ListAlgebra {
  type FList[A, B] = Unit :+: A :*: B

  def nil[A]: List[A] = Nil

  implicit def ListAlgebras[A]: InitialAlgebra[FList, List, A] with FinalCoalgebra[FList, List, A] =
    new InitialAlgebra[FList, List, A] with FinalCoalgebra[FList, List, A] {
      def fmap[B, C]: (B => C) => FList[A, B] => FList[A, C] =
        f => id[Unit] +|+ id[A] *|* f

      def out: List[A] => FList[A, List[A]] = {
        case Nil => Inl()
        case x :: xs => Inr(x :*: xs)
      }

      def inn: FList[A, List[A]] => List[A] = {
        case Inl(_) => Nil
        case Inr(x :*: xs) => x :: xs
      }
    }

    implicit class ListOps[A](self: List[A]) {

      import phi.{ListFunctions => l}

      def foldr[B](z: B)(f: (A, => B) => B): B =
        l.foldr(z)(f)(self)

//      def map[B](f: A => B): LazyList[B] =
//        l.map(f)(self)
//
//      def flatMap[B](f: A => LazyList[B]): LazyList[B] =
//        l.flatMap(f)(self)
//
//      def bind[B](f: A => LazyList[B]): LazyList[B] =
//        l.flatMap(f)(self)
//
//      def >>=[B](f: A => LazyList[B]): LazyList[B] =
//        l.flatMap(f)(self)
//
//      def take(n: Int): LazyList[A] =
//        l.take(self -> n)
//
//      def tails: LazyList[LazyList[A]] =
//        l.tails(self)
//
//      def ++[B >: A](ys: LazyList[B]): LazyList[B] =
//        l.append(ys)(self)
//
//      def +#+[B >: A](ys: LazyList[B]): LazyList[B] =
//        l.append(ys)(self)
//
//      def append[B >: A](ys: LazyList[B]): LazyList[B] =
//        l.append(ys)(self)
    }
  //
  //  implicit class LazyListOps1[A](self: LazyList[LazyList[A]]) {
  //
  //    import phi.{LazyListFunctions => l}
  //
  //    def flatten: LazyList[A] =
  //      l.flatten(self)
  //
  //    def concat: LazyList[A] =
  //      l.flatten(self)
  //  }

}

trait ListFunctions {

  type FList[A, B] = Unit :+: A :*: B

  def foldr[A, B](z: B)(f: (A, => B) => B): List[A] => B =
    cata[FList, List, A, B] {
      case Inl(_) => z
      case Inr(x :*: xs) => f(x, xs)
    }

//  def map[A, B](f: A => B): LazyList[A] => LazyList[B] =
//    hyloEta[LazyList[A], LazyList[B]] {
//      case FLazyList(Inl(_)) => FLazyList(Inl())
//      case FLazyList(Inr(x :*: xs)) => FLazyList(Inr(lazyLift(f)(x) :*: xs))
//    }
//
//  def flatten[A]: LazyList[LazyList[A]] => LazyList[A] =
//    cata[LazyList[LazyList[A]], LazyList[A]] {
//      case FLazyList(Inl(_)) => empty
//      case FLazyList(Inr(x :*: xs)) => x() ++ xs()
//    }
//
//  def flatMap[A, B](f: A => LazyList[B]): LazyList[A] => LazyList[B] =
//    hyloSigma[LazyList[A], LazyList[B], LazyList[A], LazyList[LazyList[B]]] {
//      case FLazyList(Inl(_)) => empty
//      case FLazyList(Inr(x :*: xs)) => x() ++ xs()
//    } {
//      case FLazyList(Inl(_)) => FLazyList(Inl())
//      case FLazyList(Inr(x :*: xs)) => FLazyList(Inr(lazyLift(f)(x) :*: xs))
//    }(id)
//
//  def take[A]: ((LazyList[A], Int)) => LazyList[A] =
//    ana[(LazyList[A], Int), LazyList[A]] {
//      case (Empty, _) => FLazyList(Inl())
//      case (_, 0) => FLazyList(Inl())
//      case (x :#: xs, n) => FLazyList(Inr(x :*: Lazy(xs() -> (n - 1))))
//    }
//
//  def append[A, B >: A](ys: LazyList[B]): LazyList[A] => LazyList[B] =
//    cata[LazyList[A], LazyList[B]] {
//      case FLazyList(Inl(_)) => ys
//      case FLazyList(Inr(x :*: xs)) => cons(x, xs)
//    }
//
//  def tails[A]: LazyList[A] => LazyList[LazyList[A]] =
//    para[LazyList[A], LazyList[LazyList[A]]] {
//      case FLazyList(Inl(_)) => LazyList(Lazy(empty))
//      case FLazyList(Inr(_ :*: xs)) => cons(Lazy(xs()._2), Lazy(xs()._1))
//    }
//
//  def tails2[A]: LazyList[A] => LazyList[LazyList[A]] =
//    hyloTau[LazyList[A], LazyList[LazyList[A]], LazyList[LazyList[A]], LazyList[LazyList[A]]] {
//      inn => {
//        case x@FLazyList(Inl(_)) => cons(Lazy(empty), Lazy(inn(x)))
//        case x@FLazyList(Inr(_)) => inn(x)
//      }
//    }(id[FAlgebra[LazyList[LazyList[A]], LazyList[LazyList[A]]]]) {
//      case Empty => FLazyList(Inl())
//      case xss@(_ :#: xs) => FLazyList(Inr(Lazy(xss) :*: xs))
//    }
//
//  def unfold[A, S](f: S => Option[(A, S)]): S => LazyList[A] =
//    ana[S, LazyList[A]] {
//      f(_) match {
//        case None => FLazyList(Inl())
//        case Some((a, s)) => FLazyList(Inr(Lazy(a) :*: Lazy(s)))
//      }
//    }
//
//  def constant[A]: A => LazyList[A] =
//    ana[A, LazyList[A]] {
//      x => FLazyList(Inr(Lazy(x) :*: Lazy(x)))
//    }
//
//  def from: Int => LazyList[Int] =
//    ana[Int, LazyList[Int]] {
//      x => FLazyList(Inr(Lazy(x) :*: Lazy(x + 1)))
//    }
//
//  def fibs: ((Int, Int)) => LazyList[Int] =
//    ana[(Int, Int), LazyList[Int]] {
//      case (x, y) => FLazyList(Inr(Lazy(x) :*: Lazy(y, x + y)))
//    }
}

object ListFunctions extends ListFunctions

object ListAlgebra extends ListAlgebra
