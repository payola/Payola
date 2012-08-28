package cz.payola.scala2json.classes

/**
  * Represents class of sequences (List[User], ListBuffer[Group])
  * @param seqClass Class of the sequence.
  * @param itemSerializationClass Serialization class of the items.
  */
case class SeqSerializationClass(seqClass: Class[_], itemSerializationClass: SerializationClass)
    extends SerializationClass
{
    def isClassOf(anObject: Any): Boolean = {
        seqClass.isInstance(anObject)
    }
}
