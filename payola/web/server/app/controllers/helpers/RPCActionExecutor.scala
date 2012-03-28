package controllers.helpers

import actors.Actor

/**
  *
  * @author jirihelmich
  * @created 3/13/12 2:08 PM
  * @package controllers.helpers
  */

class RPCActionExecutor extends Actor
{
    def act() {
        react {
            case RPCActionMessage(methodToRun, runnableObj, paramArray) => {
                // Store the sender of the query so the result can be sent to him later.
                val querier = sender

                methodToRun.invoke(runnableObj, paramArray:_*)

                // Wait for the expectedResultCount number of data or error messages. TimeoutMessage is recevied in
                // case the expectedResultCount messages haven't been received before the timeout.
                react {
                    case m: RPCReply => {
                        querier ! m.response
                        exit()
                    }
                }
            }
            case _ => {
                reply(None)
            }
        }
    }
}
