package s2js.runtime.client.scala.collection.immutable

object HashMap extends s2js.runtime.client.scala.collection.MapCompanion
{
    def empty = new HashMap[Any, Any]
}

class HashMap[A, B] extends s2js.runtime.client.scala.collection.Map[A, B]
{
    def newInstance = HashMap.empty
}
