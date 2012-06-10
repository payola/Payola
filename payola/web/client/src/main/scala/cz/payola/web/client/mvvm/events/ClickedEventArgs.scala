package cz.payola.web.client.events

import cz.payola.web.client.mvvm.Component

/**
 *
 * @author jirihelmich
 * @created 4/17/12 2:07 PM
 * @package cz.payola.web.client.mvvm.events
 */

class ClickedEventArgs[A](target: A) extends EventArgs[A](target)
{
}
