package cz.payola.scala2json

/** This exception class is used by the JSONSerializer. At this moment, the only
  * situation it's used in is when the serializer encounters an object cycle, while
  * the serializeInDepth option is set to true.
  *
  * @param reason Reason why the exception has been thrown.
  */
class JSONSerializationException(reason: String) extends Exception
{
    override def toString = super.toString + ": " + reason
}
