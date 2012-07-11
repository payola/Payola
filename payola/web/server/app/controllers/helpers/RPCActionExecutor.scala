package controllers.helpers

import actors.Actor

class RPCActionExecutor extends Actor
{
    def act() {
        react {
            case RPCActionMessage(methodToRun, runnableObj, paramArray) => {
                // Store the sender of the query so the result can be sent to him later.
                val querier = sender

                def successCallback(x: Any) {
                    querier ! ActionExecutorSuccess(x)
                    exit()
                }

                def failCallback(e: Throwable) {
                    querier ! ActionExecutorError(e)
                    exit()
                }

                // add param - success callback
                paramArray.update(paramArray.length-2, successCallback _)
                // add param - fail callback - takes only Throwable as a parameter
                paramArray.update(paramArray.length-1, failCallback _)

                try{
                    methodToRun.invoke(runnableObj, paramArray:_*)

                    // This means that the exit() call in success or fail callback was not reached -> none of those methods
                    // was invoked
                    failCallback(new Exception("The remote method succeeded but did not invoke the success callback."))
                }catch{
                    case e: Throwable => failCallback(e)
                }
            }
            case _ =>
        }
    }
}
