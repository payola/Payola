package s2js.runtime.scala.collection.mutable

object HashMap extends s2js.runtime.scala.collection.MapCompanion
{
    def empty = new HashMap[Any, Any]
}

class HashMap[A, B] extends s2js.runtime.scala.collection.Map[A, B]
{
    def newInstance = HashMap.empty
}
