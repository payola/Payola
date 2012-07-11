package s2js.runtime.client.scala.collection.immutable

object Seq
{
    // TODO just a hack to make the map function work.
    def canBuildFrom: Boolean = true
}

trait Seq extends s2js.runtime.client.scala.collection.Seq
