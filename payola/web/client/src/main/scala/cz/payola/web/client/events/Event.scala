package cz.payola.web.client.events

import cz.payola.web.client.mvvm_api.Component

/**
 *
 * @author jirihelmich
 * @created 4/17/12 2:08 PM
 * @package cz.payola.web.client.events
 */

class Event[+A <: Component](val target: A)
{

}
