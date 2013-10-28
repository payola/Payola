package s2js.runtime.client.scala

import s2js.runtime.client.scala.collection.immutable.StringOps

object Predef
{
    def augmentString(x: java.lang.String): StringOps = new StringOps(x)

    def unaugmentString(x: StringOps): java.lang.String = x.repr

    final def require(requirement: Boolean, message: => Any) {
        if (!requirement) {
            throw new IllegalArgumentException("requirement failed: "+ message)
        }
    }

    def refArrayOps(x: Array[Any]): Array[Any] = x

    def fallbackStringCanBuildFrom = true
}
