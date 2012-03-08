package s2js.runtime.scala

import s2js.runtime.scala.collection.immutable.StringOps

object Predef
{
    def augmentString(x: String): StringOps = new StringOps(x)

    def unaugmentString(x: StringOps): String = x.repr
}
