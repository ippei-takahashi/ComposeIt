package phi

/**
 * Created by pony on 15/05/02.
 */

import Phi._
import scala.language.experimental.macros
import scala.reflect.api.Mirror
import scala.reflect.macros.blackbox.Context

object PhiTransformer {
  def fImpl(c: Context): c.Expr[Unit] = {
    import c.{universe => u}
    import c.{mirror => m}

    val ut = u.internal.gen.mkRuntimeUniverseRef
    val mt = u.EmptyTree
    val expr = u.reify(List(1))

    val tree = c.reifyTree(ut, mt, c.typecheck(
      expr.tree
    ))

    def getTypeTag[T: u.TypeTag](obj: T) = u.typeTag[T]
    val theType = getTypeTag(Phi).tpe
    val decls: List[u.Symbol] = theType.decls.sorted
    println(theType.decls)

    u.reify(())
  }

  def f = macro fImpl
}

