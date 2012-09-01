package cz.payola.web.client.events

import collection.mutable.ArrayBuffer

class EventArgs[+A](val target: A)

/**
  * Contains the event handlers and when triggered, all event handlers are invoked. Their return values are folded
  * together and the result is returned.
  * @tparam A Type of the event target (an object that triggers the event).
  * @tparam B Type of the event arguments.
  * @tparam C Type of the event handler return value.
  */
abstract class Event[A, B <: EventArgs[A], C]
{
    private type EventHandler = B => C

    private val handlers = new ArrayBuffer[EventHandler]()

    protected def resultsFolderInitializer: C

    protected def resultsFolderReducer(stackTop: C, currentHandlerResult: C): C

    /**
      * Triggers all the event handlers and returns folded return values of the handlers.
      * @param eventArgs The arguments passed to the event handlers.
      */
    def trigger(eventArgs: B): C = {
        handlers.map(_(eventArgs)).fold(resultsFolderInitializer)(resultsFolderReducer _)
    }

    /**
      * Adds a new event handler to the event.
      * @param handler The event handler to add.
      */
    def +=(handler: EventHandler) {
        handlers += handler
    }

    /**
      * Removes the specified event handler from the event.
      * @param handler The event handler to remove.
      */
    def -=(handler: EventHandler) {
        handlers -= handler
    }

    /**
     * Removes all event handlers from the event.
     */
    def clear(){
        handlers.foreach(-=(_))
    }
}
