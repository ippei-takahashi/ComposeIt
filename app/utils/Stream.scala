package utils

/**
 * Created by pony on 15/04/30.
 */

trait Step[+A, +S]

case class Yield[+A, +S](a: => A, s: S) extends Step[A, S]

case class Skip[+S](s: S) extends Step[Nothing, S]

case object Done extends Step[Nothing, Nothing]

case class Stream[+A, +S](stepper: S => Step[A, S], initialState: S) {
  def unStream: LazyList[A] = {
    def unfoldUnStream(s: S): LazyList[A] =
      stepper(s) match {
        case Done => LazyList.empty[A]
        case Skip(s) => unfoldUnStream(s)
        case Yield(a, s) => LazyList.cons(a, unfoldUnStream(s))
      }
    unfoldUnStream(initialState)
  }

  def map[B](f: A => B): Stream[B, S] = {
    def next: S => Step[B, S] = (s: S) => stepper(s) match {
      case Done => Done
      case Skip(s) => Skip(s)
      case Yield(a, s) => Yield(f(a), s)
    }
    Stream(next, initialState)
  }
}

object Stream {
  def done[A, S]: Step[A, S] = Done
}