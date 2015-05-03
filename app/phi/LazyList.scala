package phi

/**
 * Created by pony on 15/04/30.
 */

object Lazy {
  type Lazy[+A] = () => A

  def apply[A](_value: => A): () => A = {
    lazy val value = _value
    () => value
  }

  def lazyLift[A, B]: (A => B) => Lazy[A] => Lazy[B] =
    f => x => Lazy(f(x()))
}

trait LazyList[+A] {

  import Lazy._
  import LazyList._

  def :#:[B >: A](hd: Lazy[B]): LazyList[B] = LazyList.:#:(hd, Lazy(this))

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
}

object LazyList {

  import Lazy._

  case object Empty extends LazyList[Nothing]

  case class :#:[+A](h: Lazy[A], t: Lazy[LazyList[A]]) extends LazyList[A]

  def cons[A, B >: A]: (Lazy[B], Lazy[LazyList[A]]) => LazyList[B] =
      :#:(_, _)

  def empty[A]: LazyList[A] = Empty

  def apply[A](as: Lazy[A]*): LazyList[A] =
    if (as.isEmpty)
      empty
    else
      cons(as.head, Lazy(apply(as.tail: _*)))

}
