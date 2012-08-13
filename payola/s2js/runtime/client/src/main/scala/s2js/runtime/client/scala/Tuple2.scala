/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2002-2011, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */
// GENERATED CODE: DO NOT EDIT. See scala.Function0 for timestamp.

package s2js.runtime.client.scala

import scala.collection.{TraversableLike => TLike, IterableLike => ILike}
import scala.collection.generic.{CanBuildFrom => CBF}

/** A tuple of 2 elements; the canonical representation of a [[scala.Product2]].
  *
  * @constructor  Create a new tuple with 2 elements. Note that it is more idiomatic to create a Tuple2 via `(t1, t2)`
  * @param _1   Element 1 of this Tuple2
  * @param _2   Element 2 of this Tuple2
  */
case class Tuple2[+T1, +T2](_1: T1, _2: T2)
    extends Product2[T1, T2]
{
    override def toString() = "(" + _1 + "," + _2 + ")"
}
