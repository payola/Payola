package controllers.helpers

import actors.Actor

class RPCActionExecutor extends Actor
{
    def act() {
        react {
            case RPCActionMessage(methodToRun, runnableObj, paramArray) => {
                // Store the sender of the query so the result can be sent to him later.
                val querier = sender

                var callbackCalled = false

                def successCallback(x: Any) {
                    if (!callbackCalled){
                        querier ! ActionExecutorSuccess(x)
                        callbackCalled = true
                    }
                }

                def failCallback(e: Throwable) {
                    if (!callbackCalled){
                        querier ! ActionExecutorError(e)
                        callbackCalled = true
                    }
                }

                // add param - success callback - figure out which one, though - if the callback
                // has no parameters, we need to call successCallback0
                val methodParameters = methodToRun.getParameterTypes
                val successCallbackParamType = methodParameters(methodParameters.length - 2)
                if (successCallbackParamType == classOf[scala.Function0[_]]){
                    paramArray.update(paramArray.length-2, () => successCallback(null))
                }else{
                    // At least one param - we don't currently support more than one
                    paramArray.update(paramArray.length-2, successCallback _)
                }

                // add param - fail callback - takes only Throwable as a parameter
                paramArray.update(paramArray.length-1, failCallback _)

                try{
                    methodToRun.invoke(runnableObj, paramArray:_*)

                    if (!callbackCalled){
                        failCallback(new Exception("The remote method succeeded but did not invoke the success callback."))
                    }
                }catch{
                    case e: Throwable => failCallback(e)
                }
            }
            case _ =>
        }
    }
}
