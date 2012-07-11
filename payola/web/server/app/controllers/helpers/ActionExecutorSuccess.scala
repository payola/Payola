package controllers.helpers

case class ActionExecutorSuccess(result: Any)
case class ActionExecutorError(error: Throwable)
