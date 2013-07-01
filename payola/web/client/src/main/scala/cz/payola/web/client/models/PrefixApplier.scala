package cz.payola.web.client.models

import cz.payola.common.entities.Prefix
import scala.collection.immutable

/**
 * @author Ondřej Heřmánek (ondra.hermanek@gmail.com)
 */
class PrefixApplier(/*pref: Seq[Prefix] = immutable.Seq()*/)
{
    var prefixes: Seq[Prefix] = Nil

    def applyPrefix(uri: String): String = {
        if (prefixes != Nil)
        {
            val p = prefixes.flatMap(_.applyPrefix(uri))
            p.headOption.getOrElse(uri)
        }
        else
        {
            uri
        }
    }

    def disapplyPrefix(uri: String): String = {
        if (prefixes != Nil)
        {
            prefixes.flatMap(_.disapplyPrefix(uri)).headOption.getOrElse(uri)
        }
        else
        {
            uri
        }
    }
}