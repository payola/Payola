package cz.payola.scala2json

class JSONSerializationException(reason: String) extends Exception {
    override def toString = super.toString + ": " + reason
}
