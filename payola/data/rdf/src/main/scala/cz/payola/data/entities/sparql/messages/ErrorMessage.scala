package cz.payola.data.sparql.messages

case class ErrorMessage(exception: Throwable) extends DataProviderResultMessage
