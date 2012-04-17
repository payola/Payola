package cz.payola.web.client.events

import cz.payola.web.client.mvvm_api.Component

/**
 *
 * @author jirihelmich
 * @created 4/17/12 2:07 PM
 * @package cz.payola.web.client.events
 */

class ClickedEvent[A <: Component](target: A) extends Event[A](target)
{
}
