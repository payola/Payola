/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2003-2011, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

package s2js.runtime.client.scala.util.control

class BreakControlException extends Exception

object Breaks
{
    /**
      * A block from which one can exit with a `break`. The `break` may be
      * executed further down in the call stack provided that it is called on the
      * exact same instance of `Breaks`.
      */
    def breakable(op: () => Unit) {
        try {
            op()
        } catch {
            case _: BreakControlException =>
            case ex => throw ex
        }
    }

    def break() {
        throw new BreakControlException
    }
}
