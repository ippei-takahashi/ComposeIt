//package phi
//
//import Phi._
//import Lazy._
//import LazyList._
//
///**
// * Created by pony on 15/04/30.
// */
//
//trait LazyListAlgebra extends LazyListFunctions {
//
//  implicit object FLazyList extends FAlgebra[({type λ[A, B] = Unit :+: Lazy[A] :*: Lazy[B]})#λ] {
//    def fmap: (A => B) => (Unit :+: Lazy[A] :*: Lazy[B]) => F[B]] =
//      (id[Unit] +|+ lazyLift(id[A]) *|* lazyLift(f))
//
//  }
//
//  implicit def lazyListAlgebra[A]: InitialAlgebra[LazyList[A]] with FinalCoalgebra[LazyList[A]] =
//    new InitialAlgebra[LazyList[A]] with FinalCoalgebra[LazyList[A]] {
//      def out: LazyList[A] => FAlgebra[LazyList[A], LazyList[A]] = {
//        case Empty => FLazyList(Inl())
//        case x :#: xs => FLazyList(Inr(x :*: xs))
//      }
//
//      def inn: FAlgebra[LazyList[A], LazyList[A]] => LazyList[A] = {
//        case FLazyList(Inl(_)) => empty
//        case FLazyList(Inr(x :*: xs)) => cons(x, xs)
//      }
//    }
//
//  implicit class LazyListOps[A](self: LazyList[A]) {
//
//    import phi.{LazyListFunctions => l}
//
//    def foldr[B](z: B)(f: (A, => B) => B): B =
//      l.foldr(z)(f)(self)
//
//    def foldRight[B](z: B)(f: (A, => B) => B): B =
//      l.foldr(z)(f)(self)
//
//    def map[B](f: A => B): LazyList[B] =
//      l.map(f)(self)
//
//    def flatMap[B](f: A => LazyList[B]): LazyList[B] =
//      l.flatMap(f)(self)
//
//    def bind[B](f: A => LazyList[B]): LazyList[B] =
//      l.flatMap(f)(self)
//
//    def >>=[B](f: A => LazyList[B]): LazyList[B] =
//      l.flatMap(f)(self)
//
//    def take(n: Int): LazyList[A] =
//      l.take(self -> n)
//
//    def tails: LazyList[LazyList[A]] =
//      l.tails(self)
//
//    def ++[B >: A](ys: LazyList[B]): LazyList[B] =
//      l.append(ys)(self)
//
//    def +#+[B >: A](ys: LazyList[B]): LazyList[B] =
//      l.append(ys)(self)
//
//    def append[B >: A](ys: LazyList[B]): LazyList[B] =
//      l.append(ys)(self)
//  }
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
//
//}
//
//trait LazyListFunctions {
//
//  def foldr[A, B](z: B)(f: (A, => B) => B): LazyList[A] => B =
//    cata[LazyList[A], B] {
//      case FLazyList(Inl(_)) => z
//      case FLazyList(Inr(x :*: xs)) => f(x(), xs())
//    }
//
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
//}
//
//object LazyListFunctions extends LazyListFunctions
//
//object LazyListAlgebra extends LazyListAlgebra
//
