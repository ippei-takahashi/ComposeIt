package utils

/**
 * Created by pony on 15/04/30.
 */

object Lazy {
  type Lazy[+A] = () => A

  def apply[A](v: => A): () => A = {
    lazy val value = v
    () => value
  }
}