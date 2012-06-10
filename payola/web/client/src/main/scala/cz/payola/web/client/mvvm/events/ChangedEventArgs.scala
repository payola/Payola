package cz.payola.web.client.events

import cz.payola.web.client.mvvm.Component

/**
 *
 * @author jirihelmich
 * @created 4/17/12 2:08 PM
 * @package cz.payola.web.client.mvvm.events
 */

class ChangedEventArgs[A](target: A) extends EventArgs[A](target)
{
}
