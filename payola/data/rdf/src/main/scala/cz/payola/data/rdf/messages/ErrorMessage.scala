package cz.payola.data.rdf.messages

case class ErrorMessage(exception: Throwable) extends DataProviderResultMessage
