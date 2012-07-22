package controllers.helpers

case class ActionExecutorSuccess(result: Any = null)
case class ActionExecutorError(error: Throwable)
