package utils

import Lazy._
import Stream._

/**
 * Created by pony on 15/04/29.
 */

sealed trait LazyList[+A] {

  import LazyList._

  def :#:[B >: A](hd: Lazy[B]): LazyList[B] = cons(hd(), this)

  def toList: List[A] = {
    val buf = new collection.mutable.ListBuffer[A]
    def go(xs: LazyList[A]): List[A] = xs match {
      case h :#: t =>
        buf += h()
        go(t())
      case _ => buf.toList
    }
    go(this)
  }

  def stream: Stream[A, LazyList[A]] = {
    def next: (LazyList[A]) => Step[A, LazyList[A]] = {
      case x :#: xs => Yield(x(), xs())
      case Empty => done[A, LazyList[A]]
    }
    Stream(next, this)
  }

  def foldRight[B](z: => B)(f: (=> A, => B) => B): B =
    this match {
      case x :#: xs => f(x(), xs().foldRight(z)(f))
      case _ => z
    }

  def scanRight[B](z: => B)(f: (=> A, => B) => B): LazyList[B] =
    foldRight((z, LazyList(Lazy(z))))((h, t) => t match {
      case (v, acc) =>
        val v2 = f(h, v)
        (v2, cons(v2, acc))
    })._2

  def map[B](f: A => B): LazyList[B] =
    foldRight(empty[B])((h, t) => cons(f(h), t))

  def mapUnfold[B](f: A => B): LazyList[B] =
    unfold(this) {
      case x :#: xs => Some((f(x()), xs()))
      case _ => None
    }

  def filter(p: A => Boolean): LazyList[A] =
    foldRight(empty[A])((h, t) => if (p(h)) cons(h, t) else t)

  def append[B >: A](ys: => LazyList[B]): LazyList[B] =
    foldRight(ys)(cons(_, _))

  def +#+[B >: A](ys: => LazyList[B]): LazyList[B] = append(ys)

  def flatMap[B](f: A => LazyList[B]): LazyList[B] =
    foldRight(empty[B])((h, t) => f(h) append t)


  def headOption: Option[A] =
    foldRight(None: Option[A])((h, _) => Some(h))

  def tails: LazyList[LazyList[A]] =
    unfold(this) {
      case xss@(_ :#: xs) => Some(xss, xs())
      case Empty => None
    } append LazyList(Lazy(empty))

  def take(n: Int): LazyList[A] =
    foldRight((_: Int) => empty[A])((h, t_) => (n: Int) =>
      if (n == 0) empty
      else {
        val t = t_
        cons(h, t(n - 1))
      }
    )(n)

  def takeUnfold(n: Int): LazyList[A] =
    unfold((this, n)) {
      case (x :#: xs, n) if n > 0 => Some(x(), (xs(), n - 1))
      case _ => None
    }

  def drop(n: Int): LazyList[A] =
    foldRight((_: Int) => empty[A])((h, t_) => (n: Int) =>
      if (n == 0) empty
      else {
        val t = t_
        t(n - 1)
      }
    )(n)

  def takeWhile(p: A => Boolean): LazyList[A] =
    foldRight(empty[A])((h, t) =>
      if (p(h)) cons(h, t)
      else empty
    )

  def takeWhileUnfold(p: A => Boolean): LazyList[A] =
    unfold(this) {
      case x :#: xs if p(x()) => Some(x(), xs())
      case _ => None
    }


  def exists(p: A => Boolean): Boolean =
    foldRight(false)(p(_) || _)

  def forall(p: A => Boolean): Boolean =
    foldRight(true)(p(_) && _)

  def startsWith[B >: A](ys: LazyList[B]): Boolean =
    zipAll(ys) takeWhile (_._2.nonEmpty) forall {
      case (x, y) => x == y
    }


  def zipWith[B, C](ys: LazyList[B])(f: (A, B) => C): LazyList[C] =
    unfold(this, ys) {
      case (x :#: xs, y :#: ys) => Some(f(x(), y()), (xs(), ys()))
      case _ => None
    }

  def zip[B](ys: LazyList[B]): LazyList[(A, B)] =
    zipWith(ys)((_, _))

  def zipWithAll[B, C](ys: LazyList[B])(f: (Option[A], Option[B]) => C): LazyList[C] =
    unfold(this, ys) {
      case (x :#: xs, y :#: ys) => Some(f(Some(x()), Some(y())), (xs(), ys()))
      case (x :#: xs, Empty) => Some(f(Some(x()), Option.empty[B]), (xs(), empty[B]))
      case (Empty, y :#: ys) => Some(f(Option.empty[A], Some(y())), (empty[A], ys()))
      case _ => None
    }

  def zipAll[B](ys: LazyList[B]): LazyList[(Option[A], Option[B])] =
    zipWithAll(ys)((_, _))
}

object LazyList {

  case object Empty extends LazyList[Nothing]

  case class :#:[+A](h: Lazy[A], t: Lazy[LazyList[A]]) extends LazyList[A]

  def cons[A, B >: A](hd: => B, tl: => LazyList[A]): LazyList[B] = {
    lazy val head = hd
    lazy val tail = tl
    :#:(Lazy(head), Lazy(tail))
  }

  def empty[A]: LazyList[A] = Empty

  def apply[A](as: Lazy[A]*): LazyList[A] =
    if (as.isEmpty) empty
    else cons(as.head(), apply(as.tail: _*))


  def unfold[A, S](z: S)(f: S => Option[(A, S)]): LazyList[A] =
    f(z) match {
      case None => empty
      case Some((a, s)) => cons(a, unfold(s)(f))
    }

  def constant[A](a: A): LazyList[A] =
    unfold(a)(s => Some(s, s))

  def from(n: Int): LazyList[Int] =
    unfold(n)(s => Some(s, s + 1))

  def fibs(): LazyList[Int] =
    unfold((0, 1))(s => Some((s._1, (s._2, s._1 + s._2))))
}