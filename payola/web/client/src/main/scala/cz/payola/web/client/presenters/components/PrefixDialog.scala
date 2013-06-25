package cz.payola.web.client.presenters.components

import cz.payola.web.client.views.bootstrap.Modal
import cz.payola.web.client.views.elements.{Div, Text}
import scala.collection.immutable.List
import scala.Some

/**
 * @author Ondřej Heřmánek (ondra.hermanek@gmail.com)
 */
class PrefixDialog(prefix: String, url: Option[String])
    extends Modal("Prefix found", Nil, Some("OK"), None, false)
{
    // Create info message box based on whether prefix mapped to url or not
    val text =
        url.map(
            "You have used an unknown prefix in uri '%s', it has been associated to '%s' url and added to your prefixes.".format(prefix, _)
        ).getOrElse(
            "You have used an unknown prefix in uri '%s', which has not been mapped to any url.".format(prefix)
        )
    val label = new Text(text)
    val placeholder = new Div(List(label))

    override val body = List(placeholder)
}
